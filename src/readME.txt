When running the app, since I am using file based H2, I set spring.sql.init.mode=always in application.properties,
only for the first time its run, after which the schema is created and this property has hence been commented out.

Run the application from TodoApplication.java which is the runner class and picks up the static elements in 
the project

The application can be accessed from http://localhost:8080/ which runs the todo list, there are 2 default
user name and password combinations set in the data.sql:
1) username: user1, password: password1
upon entering this, the user can log in and the previously stored todos appear, they are sorted in descending order
of ids (which means ascending order of creation for convenience)
the database can be accessed from http://localhost:8080/h2-console/  with the URL jdbc:h2:file:./data/tododata 

now items can be added, toggled, deleted, updated

Moreover, the app supports pagination as well as offline mode and storage, since when the app stops running,
the todos items are still saved and the items on re-running the application are retrieved again, causing
no loss of data 