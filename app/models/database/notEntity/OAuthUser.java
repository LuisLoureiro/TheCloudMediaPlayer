package models.database.notEntity;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import play.data.validation.Constraints.Required;
import models.database.User;

@MappedSuperclass
public abstract class OAuthUser extends User
{
	@Column(name="provider_name", nullable=false)
	@Required(message="The provider's name could not be null")
	private String providerName;
	
	public String getProviderName() {
		return providerName;
	}
	public void setProviderName(String providerName) {
		this.providerName = providerName;
	}
}
