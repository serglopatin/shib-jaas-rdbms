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

Copy shibjaasrdbms.jar to shibboleth libs and rebuild war file:
```
sudo cp shibjaasrdbms.jar /opt/shibboleth-idp/webapp/WEB-INF/lib/
sudo JAVACMD=/usr/bin/java /opt/shibboleth-idp/bin/build.sh -Didp.target.dir=/opt/shibboleth-idp
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
