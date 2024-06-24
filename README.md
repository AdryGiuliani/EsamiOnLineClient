# Progetto Ing-sw distribuito Esami-online (modulo frontend)
Il frontend del progetto è stato realizzato utilizzando Jboss/WIldfly 32 , Jakarta Faces, PrimeFaces, Primeflex e FontAwesome, per far partire il server è sufficiente:

1. Scaricare la root directory wildfly : https://github.com/wildfly/wildfly/releases/download/32.0.1.Final/wildfly-32.0.1.Final.zip
2. Aprire il progetto
3. Aggiungere server jboss/wildfly come configurazione di run indicando come directory quella scaricata (ed estratta) in precedenza (eventualmente cambiando config di wildfly)
4. Creare configurazione di run che avvii: main app.Application (start del grpc server) + wildfly
5. è possibile collegarsi al frontend /login.xhtml è il login per client, /admin.xhtml la controparte di amministrazione.

Il lato backend è inserito come dipendenza maven all'interno del pom e disponibile al repository: https://github.com/AdryGiuliani/EsamiOnLine, potreppe essere necessario effettuare, dopo aver scaricato il backend, mvn clean install per rendere visibile il repository backend anche lato frontend.
