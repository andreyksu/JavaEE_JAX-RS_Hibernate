Instruction
    0.  Download the WildFly and Postgre driver.
            Unpack the WildFly.

	1. 	Add jdbc driver to Wildfly:
			1.1. Add structure of directories:
				$WILDFLY_HOME/modules/org/postgresql/main
			
			1.2. Copy into this directory two files:
				- module.xml
				- jdbc driver

	2.	Insert into standalone.xml (Or in  standelone-full.xml, then you should run WildFly like : ${WILDFLY_HOME}\bin\standalone.bat -c standalone-full.xml)

			<subsystem xmlns="urn:jboss:domain:datasources:6.0">
				<datasources>
					<datasource jndi-name="java:jboss/datasources/ExampleDS" pool-name="ExampleDS" enabled="true" use-java-context="true" statistics-enabled="${wildfly.datasources.statistics-enabled:${wildfly.statistics-enabled:false}}">
						<connection-url>jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE</connection-url>
						<driver>h2</driver>
						<security>
							<user-name>sa</user-name>
							<password>sa</password>
						</security>
					</datasource>
	>>>				<datasource jta="true" jndi-name="java:jboss/datasources/DataSourceEx" pool-name="DataSourceEx" enabled="true" use-java-context="true">
						<connection-url>jdbc:postgresql://192.168.0.111:5432/testdb</connection-url>
						<driver>postgresql-jdbc4</driver>
						<pool>
							<min-pool-size>5</min-pool-size>
							<max-pool-size>20</max-pool-size>
						</pool>
						<security>
							<user-name>testov</user-name>
							<password>testov</password>
						</security>
	>>>				</datasource>
					<drivers>
						<driver name="h2" module="com.h2database.h2">
							<xa-datasource-class>org.h2.jdbcx.JdbcDataSource</xa-datasource-class>
						</driver>
	>>>					<driver name="postgresql-jdbc4" module="org.postgresql">
							<xa-datasource-class>org.postgresql.xa.PGXADataSource</xa-datasource-class>
	>>>					</driver>
					</drivers>
				</datasources>
			</subsystem>

			В datasource указывается какой driver будет исползоваться (см. <driver>postgresql-jdbc4</driver>) , а в driver указывается какой модуль будет его обслуживать module (см. module="org.postgresql" и файл module.xml)
			По сути здесь описывается физическое подключение к БД.
			А вот persistance.xml - описывает именно то что относится к Hibernate. В persistance.xml маппирование на физическое подключение осущетвляется через <jta-data-source>.
			    По сути описывается то, что указано в xxx.ds.xml

	3.	For first and second clause we use the persistance.xml

	4. Create database and tables.
	
JMS
	1. Настроить работу с JMS в конфигурационном файле standelone.xml или standelone-full.xml
		- По умолчанию в standelone.xml не добавлена поддержка JMS для этого нужно в первую очередь добавить поддержку JMS после чего уже добавлять очереди и настраивать под свои нужды.
			Или непосредственно в standelone.xml добавить элемент:
			<extensions>
			    ...
				<extension module="org.wildfly.extension.messaging-activemq"/>
				...
			</extensions>

			Или с использованием cli скрипта:
				extension=org.wildfly.extension.messaging-activemq:add

		- В данном проекте использовал standelone-full.xml - в котором уже есть поддержка activemq. Для использования standelone-full.xml нужно выполнить заупск в виде ${WILDFLY_HOME}\bin\standalone.bat -c standalone-full.xml
		     Теперь просто нужно добавить очередь!

		Примечание:
		    В секции <subsystem xmlns="urn:jboss:domain:messaging-activemq:13.0">
		        - acceptors (т.е. <http-acceptor> или <in-vm-acceptor>) - описывают какие порты слушать и какие протоколы  задействованы.
		        - connectors (т.е. <http-acceptor> итд) - по каким протоколам коннектится, к каким портам.
		
		В секцию:
			<subsystem xmlns="urn:jboss:domain:messaging-activemq:13.0">
		Добавить:
			<jms-queue name="FirstQueue" entries="queue/ForTest java:/jms/queue/ForTest"/>


Настройка БД
1. Установка СУБД
2. Задание пароля для пользователя postgres
		sudo -u postgres psql
		\password postgres

		или
		sudo -u postgres psql -c "ALTER USER postgres PASSWORD '<new-password>';"

3. Создание пользователя:
	psql -U postgres -d postgres -h localhost
		CREATE USER testov WITH ENCRYPTED PASSWORD 'testov';
		CREATE DATABASE testdb with owner = testov encoding = 'UTF8' tablespace = pg_default  lc_collate = 'ru_RU.UTF-8' LC_CTYPE = 'ru_RU.UTF-8'; --GRANT ALL PRIVILEGES ON DATABASE yourdbname TO youruser;
			
	Авторизоваться под созданным пользователем в созданную базу:
		psql -U testov -d testdb -h localhost

CREATE TABLE mother
(id serial NOT NULL,
mother_name character varying(100) NOT NULL,
mother_old integer NOT NULL,
CONSTRAINT mother_pkey PRIMARY KEY (id)
);

CREATE TABLE wife
(
id serial NOT NULL,
wife_name character varying(100) NOT NULL,
wife_old integer NOT NULL,
CONSTRAINT wife_pkey PRIMARY KEY (id)
);

CREATE TABLE person
(
id serial NOT NULL,
person_name character varying(100) NOT NULL,
person_old integer NOT NULL,
wife_id integer,
mother_id integer,
CONSTRAINT person_pkey PRIMARY KEY (id),
CONSTRAINT fk_mother_id FOREIGN KEY (mother_id) REFERENCES mother (id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION,
CONSTRAINT fk_wife_id FOREIGN KEY (wife_id) REFERENCES wife (id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION
);

CREATE TABLE pet
(
id serial NOT NULL,
pets_name character varying(100) NOT NULL,
pets_old integer NOT NULL,
person_id integer,
CONSTRAINT pet_pkey PRIMARY KEY (id)
);

-------------------------
Обращение к сервису:
http://<hostname>:8080/try-jee/rest/...