package com.cda.entities;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Tenant.class)
public abstract class Tenant_ {

	public static volatile SingularAttribute<Tenant, String> password;
	public static volatile SingularAttribute<Tenant, String> id;
	public static volatile SingularAttribute<Tenant, String> packageName;
	public static volatile SingularAttribute<Tenant, String> db;
	public static volatile SingularAttribute<Tenant, String> url;

	public static final String PASSWORD = "password";
	public static final String ID = "id";
	public static final String PACKAGE_NAME = "packageName";
	public static final String DB = "db";
	public static final String URL = "url";

}

