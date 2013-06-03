package models.beans;

import java.util.List;

public class ServiceResources
{
	private String serviceName;
	private List<Resource> resources;
	
	public ServiceResources(String serviceName, List<Resource> resources)
	{
		setServiceName(serviceName);
		setResources(resources);
	}
	
	public String getServiceName() {
		return serviceName;
	}
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	public List<Resource> getResources() {
		return resources;
	}
	public void setResources(List<Resource> resources) {
		this.resources = resources;
	}
}
