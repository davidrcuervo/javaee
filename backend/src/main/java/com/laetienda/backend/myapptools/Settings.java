package com.laetienda.backend.myapptools;

public final class Settings {
	
	public static int DB_PORT = 5432;
	public static String HOSTNAME = "localhost";
	public static String DB_USERNAME = "web";
	public static String DB_AES_PASSWORD = "AI3DvVEl1RuUNzvewBLBPRFfcTZJQ5MGKIiML8nNDJMyBrthg0bHAyLQNetFhx3IUk1O+A==";
	public static String DB_PERSISTENCE_UNIT_NAME = "com.laetienda.database";
	public static int LDAP_PORT = 6363;
	public static String LDAP_DOMAIN = "dc=example,dc=com";
	public static String LDAP_ADMIN_USER = "cn=admin," + LDAP_DOMAIN;
	public static String LDAP_PEOPLE_DN = "ou=People," + LDAP_DOMAIN;
	public static String LDAP_TOMCAT_DN = "uid=tomcat," + LDAP_PEOPLE_DN;
	public static String LDAP_ADIN_AES_PASSWORD = "hrZPTnRKVB2XChvK54XyJwmIXdfVLynGkn2oPTOpMNqMozh1GJ3vEVGbhobjVluyj/Qi8g==";
	public static String SYSADMIN_AES_PASS = "IkWlRQaCxCnS5xH7ZS1Vmex1UbunOp425j4k2gsNDudeSohHe5NqKpljZsXg2cTP19PQDA==";
	public static String TOMCAT_AES_PASS = "7ZXPbcXMzhLKFs2HIkg+KmLjYvhc3hoUa2FrXf9+jsyvDC6AW9b5kwSc0Vb6hQSgQY2i1Gj/wQ8ucZBmZ1zwKZ3Gl9k=";
	public static String MANAGER_AES_PASS = "UYxRAtB4kknbm587ufbdY19LzyS7tH/XosAz9dvWbo7BCvqZs8kve4dpGaJeIt4I88Dinh9E2/PjMYfUZlcZCl1uyv0=";
}
