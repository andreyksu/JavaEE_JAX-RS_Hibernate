# Краткое описание.

- Аппробация на практике **JEE** на базе **WildFly**
	* JAX-RS
	* JPA(Hibernate) + PostgreSQL
	* JMS/MDB
	* CDI-Interceptors
- В для аппробации JAX-RS--EJB---Hibernate выбрано приложение в виде - *Чат*
	* Для *чата* см. пакеты **ru.annikonenkov.rs.message.**, где входной точкой для *чата* является пакет **ru.annikonenkov.rs.message.rest**
		* На данный момент: в WildFly сделан только DynamicContent. StaticContent контент предполагается отдавать через Apache или nGinx (*если будет добавлено*);
		* Аналогично и SSL (*если будет добавлено*);
	* Остальные пакеты, добавлялилсь (так и остались) для аппробации JMS/EJB-Timers/CDI Interceptors. В будущем можно/нужно удалить.
- *Аутентификация* выполняется через **KeyCloak**
	- На стороне app-server связка: WildFly + Eletron
- *Авторизация* выполняется на стороне **WildFly** (на базе ролей, что добавлены в **KeyCloak**)

## Настройка KyeCloak
- Настройка(связка) **keyCloak-19.0.1** и **WildFly_22.0.0**
	- Начиная с **WildFly_25** - в составе уже есть *elytron* (и возможно, настройка будет отличаться от того, что представлено ниже. Т.е. возможно дополнительно адаптер уже не потребуется разворачивать).

1. Скачиваем адаптер для **KeyCloak** далее - *KeyCloak-oidc-wildfly-adatper.tgz*;
2. Cкаченный адаптер распаковываем в корень: `$WILDFLY_HOME`;
3. Выполняем **cli**-скрипт (здесь зависит от того какой конфигурационный файл будет использоваться);
    
    3.1. Далее в проекте используется `standalone-full.xml` - по этому  нужно воспользоваться командой;
	```sh
	$WILDFLY_HOME/bin/jboss-cli.sh --file=adapter-elytron-install-offline.cli -Dserver.config=standalone-full.xml;
	```
	3.2. Если используется `standalone.xml` - то выполняется команда без указания файла;
	```sh
	$WILDFLY_HOME/bin/jboss-cli.sh --file=adapter-elytron-install-offline.cli;
	```
4. Скачиваем сам **keyCloak** (19.0.1 - уже на базе Quarkus)

	4.1. Распаковываем и запускаем (на отдельном порту и в режиме разработки):
	```sh
		/opt/keycloak-19.0.1/bin/kc.sh start-dev --http-port 9099  --http-relative-path /auth
	```
	4.2. Если к KeyCloak есть доступ по `localhost` - то возможно перейти по адресу **KeyCloak** и задать пароль для **admin**:
    - В ином случае перед запуском экспортируем Linux переменные:
        ```sh
        export KEYCLOAK_ADMIN=admin
        export KEYCLOAK_ADMIN_PASSWORD="somePass"
        ```
	- После чего запускаем KeyCloak и можно авторизоваться не с localhost

	4.3. Создаем в KeyCloak новый Realm:
    - Внутри Realm создаем:
	    - сlient (олицетворяет сервис - в нашем случае это наш JBoss т.е. наше приложение);
		- user (тот под кем будем авторизовываться);
		- RealmRoles (и эту роль предоставляем созданному пользователю далее указывается в web.xml);
			- Доступ к ресурсу предоставляется роли (а через эту роль уже пользователь получает возможность получить доступ к ресурсу).
		- Переходим в client - и выгружаем JSON (Download adapter config)
		
	4.4. Выгруженный JSON размещаем в WEB-INF созданного проекта.
	4.5. Там же создаем web.xml с настройками авторизацией на keyCloak.

## Настройка Самого JBoss:
1.  Download the WildFly and Postgre driver.
    - Unpack the WildFly.
2. 	Add jdbc driver to Wildfly:

	2.1. Add structure of directories:
	```sh
		$WILDFLY_HOME/modules/org/postgresql/main
	```	
	2.2. Copy into this directory two files:	
		- module.xml
		- jdbc driver

3.	Insert into `standalone.xml`
    - Or in  standelone-full.xml, then you should run WildFly like:
        ```sh
        ${WILDFLY_HOME}\bin\standalone.bat -c standalone-full.xml
        ```

        ```xml
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
        ```

    - В datasource указывается какой driver будет исползоваться (см. `<driver>postgresql-jdbc4</driver>`) , а в **driver** указывается какой модуль будет его обслуживать **module** (см. `module="org.postgresql"` и файл **module.xml**);
    - По сути здесь описывается физическое подключение к БД;
    - А вот **persistance.xml** - описывает именно то что относится к Hibernate. В persistance.xml маппирование на физическое подключение осущетвляется через `<jta-data-source>`. По сути описывается то, что указано в **xxx.ds.xml**

3. For first and second clause we use the persistance.xml
4. Create database and tables.
	
## Настройка JMS
1. Настроить работу с JMS в конфигурационном файле standelone.xml или standelone-full.xml
	- По умолчанию в standelone.xml не добавлена поддержка JMS для этого нужно в первую очередь добавить поддержку JMS после чего уже добавлять очереди и настраивать под свои нужды.
		- Непосредственно в `standelone.xml` добавить элемент:
    		```xml
    		<extensions>
    		    ...
    			<extension module="org.wildfly.extension.messaging-activemq"/>
    			...
    		</extensions>
            ```
		- Или с использованием **cli** скрипта:
    		```sh
    			extension=org.wildfly.extension.messaging-activemq:add
    		```

		- В данном проекте использовал ```standelone-full.xml``` - в котором уже есть поддержка ```activemq```. Для использования ```standelone-full.xml``` нужно выполнить заупск в виде 
    		```sh
    		${WILDFLY_HOME}\bin\standalone.bat -c standalone-full.xml
    		```
	- Теперь просто нужно добавить очередь!

		- Примечание:
            * В секции `<subsystem xmlns="urn:jboss:domain:messaging-activemq:13.0">`
                * acceptors (т.е. <http-acceptor> или <in-vm-acceptor>) - описывают какие порты слушать и какие протоколы  задействованы.
                * connectors (т.е. <http-acceptor> итд) - по каким протоколам коннектится, к каким портам.
		
		- В секцию `<subsystem xmlns="urn:jboss:domain:messaging-activemq:13.0">`
		    Добавить `<jms-queue name="FirstQueue" entries="queue/ForTest java:/jms/queue/ForTest"/>`


## Настройка БД
1. Установка СУБД
2. Задание пароля для пользователя postgres

    ```sh
    sudo -u postgres psql
    \password postgres
    ```
    или
    
    ```sh
    sudo -u postgres psql -c "ALTER USER postgres PASSWORD '<new-password>';"
    ```

3. Создание пользователя:
    ```
    psql -U postgres -d postgres -h localhost
    ```

    ```
    CREATE USER testov WITH ENCRYPTED PASSWORD 'testov';
    CREATE DATABASE testdb with owner = testov encoding = 'UTF8' tablespace = pg_default  lc_collate = 'ru_RU.UTF-8' LC_CTYPE = 'ru_RU.UTF-8';
    --GRANT ALL PRIVILEGES ON DATABASE testov TO testov;
    ```
		
4. Авторизоваться под созданным пользователем в созданную базу:
    ```
	psql -U testov -d testdb -h localhost
    ```

### Создание схемы:
1. Схема создается непосредственно Hibernate.
	1.1. Такой подход был обусловлен аппробацией возможностей Hibernate (посмотреть, как задаются дефолтные значения, задаются наименования ограничениям итд).
			Т.к. схема состоит не более чем из 5-6 таблиц, можно создать их "руками".
	1.2. Для старта приложения, необходимо просто задеплоить приложение (поместив .war в каталог deployments) и запустить JBoss.

---
Обращение к сервису:
` http://<hostname>:8080/try-jee/rest/chat/...  `
` http://192.168.0.154:8080/try-jee/ ` к html



# TODO:
1. Сделать добавление пользователей через пользователя с правими Admin (т.е. предоставить отдельного пользователя, что будет добавлен при деплое приложения, через которого можно будет добавлять пользователей):
	- Добавить аннотации @RolesAllowed({"Admin"}) на соответствующие методы для таких пользователей-админов (для доступа к таким points только привелигированным пользователям-админам);
	- Аналогично такой пользователь-админ может блокировать пользователей, удалять пользователей, восстанавилвать пользователей итд. итп;
2. Пробежаться по коду
	- Убарть лишний код (есть дублирование см. класс RestForMessages - для групп и для пользователей одинаковый код - нужно вынести отдельно);
	- Продумать исключение (изначально делал исключения по типу - но в итоге в самом RestForMessages - пришел к тому, что перехватываю все исключения т.к. тип исключения отдаваемый на клиент мне уже не важен.)
3. Добавить Java-code для методов и классов;
4. Для файлов добавить сохранение имени файла в БД и добавить наименование при скачивании файла;
5. Проверить/добавить ограничение на размер файла:
	- Прочитать первые 6МБ и если реальный размер больше 5.5МБ - то выкидывать сообщение;
6. 	Автоматизировать действия и проверку через Postman;
	- Как позитивные сковозные кейсы, так и негативные (заиспользовать JS);
	- Параллельно с этим продумать и ДОВОДИТЬ до ума работу сервера:
		- Где и что может упасть, где и что может пойти не так, в том числе и параллельная работа - доступ к одним и тем-же ресурсам итд;
7. Попробовать для одного из методов сделать Remote EJB и подёграть клиентом это EJB:
	- Достаточно просто попробовать (чтоб было в голове как это делается);
8. Продумать по параллельности:
	- Где и как могут возникнуть проблемы;
	- Продумать в части CDI - по их Scoupe и время их жизни и привязку к запросу;
9. Различные инструменты в части автоматизации:
	- Автоматизация через Java Assured (TestNG, JUnit);
	- Автоматизация через Python Requests, Python Locust (Pytest);
	- Автоматизация через JMeter и Gatling (Здесь стоит начать с Gatling);	
		- Все что касается Postman, PythonRequests, Java Assured сложить все рядом - как соседние проекты + ссылка на исходный проект, что тестируется.
10. Добавить проверку размера файла (допустим 5МБ).
	- Получить и прочитать первые 5.5МБ и если кол байт больше 5.2МБ то выкидывать исключение или получать размер из POST - запроса.
11. Добавить ограничение на длину сообщения. Не более 1000 символов.
12. SWAGER:
	- Сделать Документацию;
13. Mockito:
	- Заюзать.