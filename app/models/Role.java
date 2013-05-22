package models;


public enum Role implements be.objectify.deadbolt.core.models.Role {
	ADMIN,
	CONTRIBUTOR,
	DELETED;

	@Override
	public String getName() {
		return this.name();
	}
}
