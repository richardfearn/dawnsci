package org.eclipse.dawnsci.remotedataset.server.event;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.FileTime;
import java.util.List;

import org.eclipse.dawnsci.analysis.api.dataset.DataEvent;
import org.eclipse.dawnsci.analysis.api.dataset.IDynamicDataset;
import org.eclipse.dawnsci.analysis.api.dataset.ILazyDataset;
import org.eclipse.dawnsci.analysis.api.io.IDataHolder;
import org.eclipse.dawnsci.analysis.api.monitor.IMonitor;
import org.eclipse.dawnsci.remotedataset.ServiceHolder;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileMonitorSocket extends WebSocketAdapter {
	
	private static final Logger logger = LoggerFactory.getLogger(FileMonitorSocket.class);
	
	private boolean            connected;


	@Override
     public void onWebSocketConnect(Session sess) {
 		
		connected = true;
		final String spath = getFirstValue(sess, "path");
		final String sset  = getFirstValue(sess, "dataset");
		final Path   path  = Paths.get(spath);
		try {
			WatchService myWatcher = path.getFileSystem().newWatchService();
			
			QueueReader fileWatcher = new QueueReader(myWatcher, sess, spath, sset);
	        Thread th = new Thread(fileWatcher, path.getFileName()+" Watcher");
	        
	        // We may only monitor a directory
	        if (Files.isDirectory(path)) {
	        	path.register(myWatcher, ENTRY_CREATE, ENTRY_MODIFY, ENTRY_DELETE);
	        } else {
	            path.getParent().register(myWatcher, ENTRY_CREATE, ENTRY_MODIFY, ENTRY_DELETE);
	        }
	        th.start();
	 
    	} catch (Exception ne) {
			ne.printStackTrace();
			try {
				sess.getRemote().sendString(ne.getMessage());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}

	private String getFirstValue(Session sess, String name) {
		final List<String> vals = sess.getUpgradeRequest().getParameterMap().get(name);
		return vals!=null?vals.get(0):null;
	}

	@Override
    public void onWebSocketClose(int statusCode, String reason) {
		super.onWebSocketClose(statusCode, reason);
		connected = false;
	}

    private class QueueReader implements Runnable {
    	 
        /** the watchService that is passed in from above */
        private WatchService watcher;
        private Session      session;
		private String spath;
		private String sdataset;
		
        public QueueReader(WatchService watcher, Session session, String path, String dataset) {
            this.watcher    = watcher;
            this.session    = session;
            this.spath      = path;
            this.sdataset   = dataset;
        }
 
        /**
         * In order to implement a file watcher, we loop forever 
         * ensuring requesting to take the next item from the file 
         * watchers queue.
         */
        @Override
        public void run() {
        	
        	final Path   path  = Paths.get(spath);
            try {       			
       			// We wait until the file we are told to monitor exists.
       			while(!Files.exists(path)) {
       				Thread.sleep(200);
       			}
       			
                // get the first event before looping
                WatchKey key = null;
                while((key = watcher.take()) != null) {
                                 		
             		try {
                 		if (!Files.exists(path)) continue;
                 		
	            		for (WatchEvent<?> event : key.pollEvents()) {
	            			
		             		if (!Files.exists(path)) continue;
	
	 	             		Path   epath = (Path)event.context();
	 	             		if (!Files.isDirectory(path) && !path.endsWith(epath)) continue;
	 	             			 	             		
	 	             		try {
			             		// Data has changed, read its shape and publish the event using a web socket.
			             		final IDataHolder  holder = ServiceHolder.getLoaderService().getData(spath, new IMonitor.Stub());
						        if (holder == null) continue; // We do not stop if the loader got nothing.
			        			
						        final ILazyDataset lz = sdataset!=null && !"".equals(sdataset)
						                              ? holder.getLazyDataset(sdataset)
						                              : holder.getLazyDataset(0);
			             		
						        if (lz == null) continue; // We do not stop if the loader got nothing.
						        
						        if (lz instanceof IDynamicDataset) {
						            ((IDynamicDataset)lz).refreshShape();	
						        }
						        
						        
			                	final DataEvent evt = new DataEvent(lz.getName(), lz.getShape());
			                	evt.setFilePath(spath);
			                    
			                    // We manually JSON the object because we
			                	// do not want a dependency and object simple
			                	String json = evt.encode();
			                	session.getRemote().sendString(json);
			                	
	 	             		} catch (Exception ne) {
	 	             			logger.error("Exception getting data from "+path);
	 	             			continue;
	 	             		}
		                	break;
	            		}
	            		
             		} finally {
                    	key.reset();
                    	
                    	if (!session.isOpen() || !connected) {
                    		break;
                    	}
             		}
                }
                
            } catch (Exception e) {
            	logger.error("Exception monitoring "+path, e);
            	session.close(403, e.getMessage());
            } 
        }
    }

}