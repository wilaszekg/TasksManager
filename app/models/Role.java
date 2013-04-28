package models;


public enum Role implements be.objectify.deadbolt.core.models.Role {
	ADMIN,
	CONTRIBUTOR;

	@Override
	public String getName() {
		return this.name();
	}
}
