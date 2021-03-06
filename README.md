# shib-jaas-rdbms
JAAS RDBMS authentication module for Shibboleth Identity Provider with python hash verification

##Requirements
- Shibboleth Identity Provider 3+
- python 2.7 + requirements.txt
- openjdk-7-jdk

##Java module compilation
```
git clone https://github.com/serglopatin/shib-jaas-rdbms
cd shib-jaas-rdbms
make
```

##Shibboleth configuration
Install python and requirements:
```
virtualenv venv
source venv/bin/activate
pip install passlib
```

Change auth method in **/opt/shibboleth-idp/conf/authn/password-authn-config.xml**:
```
<import resource="jaas-authn-config.xml" />
<!--<import resource="ldap-authn-config.xml" />-->
```

Create jaas config in **/opt/shibboleth-idp/conf/authn/jaas.config**:
```
ShibUserPassAuth {
        com.shibjaasrdbms.ShibRdbmsLogin required
        dbDriver="org.postgresql.Driver"
        dbUrl="jdbc:postgresql://localhost/mydb"
        dbUser="mydbuser" dbPassword="123" dbTable="user"
        dbColumnPw="password" dbColumnLogin="name"
        pyExePath="/home/test/shib-jaas-rdbms/venv/bin/python"
        pyModulePath="/home/test/shib-jaas-rdbms/src-py/hashverifier.py";
};

```

Copy shibjaasrdbms.jar to Shibboleth libs, rebuild war file and restart webserver:
```
sudo cp shibjaasrdbms.jar /opt/shibboleth-idp/webapp/WEB-INF/lib/
sudo JAVACMD=/usr/bin/java /opt/shibboleth-idp/bin/build.sh -Didp.target.dir=/opt/shibboleth-idp
sudo service tomcat7 restart
```

## Python hash verification
Default hash algorithms defined in hashverifier.py as:
```
myctx = CryptContext(schemes=["pbkdf2_sha256", "pbkdf2_sha512"])
```
You can change hash algorithm to any value supported by passlib.
