package org.eclipse.dawnsci.remotedataset.client;

import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;

import org.eclipse.dawnsci.analysis.api.dataset.IRemoteDataset;
import org.eclipse.dawnsci.analysis.api.io.IRemoteDatasetService;
import org.eclipse.dawnsci.remotedataset.Format;
import org.eclipse.dawnsci.remotedataset.client.dyn.DynamicDatasetFactory;
import org.eclipse.dawnsci.remotedataset.client.dyn.IDynamicMonitorDataset;
import org.eclipse.dawnsci.remotedataset.client.slice.SliceClient;

public class RemoteDatasetServiceImpl implements IRemoteDatasetService {

	static {
		System.out.println("Starting remote dataset service.");
	}
	@Override
	public IRemoteDataset createRemoteDataset(String serverName, int port) {
    	return new RemoteDataset(serverName, port, getExecutor());
	}
	
	@Override
	public IRemoteDataset createMJPGDataset(URL url, long sleepTime, int cacheSize) throws Exception {
		SliceClient<BufferedImage> client = getSlice(url, sleepTime, cacheSize);
		final IDynamicMonitorDataset rgb = DynamicDatasetFactory.createRGBImage(client);
    	return rgb;
	}

	@Override
	public IRemoteDataset createGrayScaleMJPGDataset(URL url, long sleepTime, int cacheSize) throws Exception {
		SliceClient<BufferedImage> client = getSlice(url, sleepTime, cacheSize);
		final IDynamicMonitorDataset rgb = DynamicDatasetFactory.createGreyScaleImage(client);
    	return rgb;
	}

	private SliceClient<BufferedImage> getSlice(URL url, long sleepTime, int cacheSize) {
		SliceClient<BufferedImage> client = new SliceClient<BufferedImage>(url);
		client.setGet(false);
    	client.setFormat(Format.MJPG);
    	client.setImageCache(cacheSize); // More than we will send...
    	client.setSleep(sleepTime);
		return client;
	}

	private static Executor executor;
	
	@Override
	public void setExecutor(Executor exec) {
		executor = exec;
	}

	@Override
	public Executor getExecutor() {
		if (executor==null) executor = new ForkJoinPool();
		return null;
	}
}
