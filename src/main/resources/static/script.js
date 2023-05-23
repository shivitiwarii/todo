var app = Vue.createApp({
  data() {
    return {
      todoList: [],
      newTodo: '',
      hideOrShow: true,
      activePage: 1,
      insertNew: false,
      username:'',
      password:'',
      authenticated: false,
      toUpdateId: null, 
      updatedText: '' ,
      page: 1,
      size: 5
    }
  },
  methods: {
     //when the user starts the app, they should login with user name and password
     login: async function () {
      const thiss = this;
      try {
        //sends a post request to /login endpoint in the backend to authenticate the user
        await axios.post('/api/todos/login', { username: thiss.username, password: thiss.password }); //username and password are modelled in the index.html

        //if authentication is successful, authenticated is set to true
        thiss.authenticated = true;  
        response = await thiss.loadTodos();  //loadTodos is called to load the todos from the database even if the app is re run
        //the todoList is set to the todoItems returned from the loadTodos method
        thiss.todoList = response.todoItems; 
      } catch (error) {
        alert('Login failed, try again'); //if authentication fails, an alert is displayed
      }
    },

    //this method is called immediately after a successful login
    async loadTodos(pageNumber = 1, pageSize = 5) {
      try {
        //uses the getUser method to get the user details in order to load their previously saved todos
        user = await this.getUser();
        //sends a get request to the /api/todos with parameters for pagination endpoint in the backend to get the todos
        response = await fetch(`/api/todos?page=${pageNumber - 1}&size=${pageSize}`, {
          method:"GET",  headers: {
            "Content-Type": "application/json",
            "Accept": "application/json",
            "withCredentials": "true",
          }
        });
        //the response is converted to json
        const responseText = await response.json(); 

        //the todo items are extracted from the response
        todoItems = responseText.content;
        activePage = responseText.number + 1;
        totalPages = responseText.totalPages;

        //items are assigned to the user which are saved in db
        todoItems.forEach(todoItem => {
          todoItem.user = user; });
        return {
          //this is set so that all other oprations can be performed on the todo items
          todoItems: todoItems,
          totalPages: totalPages,
          activePage: activePage,
        };
      } catch (e) {
        throw e;
      }
    },
    async getUser() {
      try {
          response = await fetch('/api/todos/user', { //sends a get request to the /api/todos/user endpoint in the backend to get the user details
          method:"GET",headers: {
            "Content-Type": "application/json",
            "Accept": "application/json",
            "withCredentials": "true",
          }
        });
        responseData = await response.json(); //the response is converted to json
        return responseData;
      } catch (e) {
        throw e;
      }
    },
    addTodo() {
      const thiss = this;
      if(!this.newTodo) {
        return;
      }
      //sends a post request to the /api/todos endpoint in the backend to add a new todo item
      fetch('/api/todos',{
        method:'PUT',
        headers: {
          'Content-Type': 'application/json',
        },
        //the new todo item is sent as a json object
        body: JSON.stringify({ content: thiss.newTodo }),
      })
      .then(function(response) {
        //if the response has error, an error is thrown
        if (!response.ok) {
          throw new Error('Error occured in insertion');
        }
        return response.json();
      })
      .then(function(data) {
        //the new todo item is added to the todoList
        thiss.todoList.push(data);
        thiss.newTodo = '';   //the newTodo is set to empty string again
      }).catch(function(err) {
        throw err;
      });
    },

    //this method is called when the user clicks on the hide/show completed todos button
    toggleCompletion: function(id) {
      const thiss = this;
      //sends a patch request to the /api/todos endpoint in the backend to update the state
      axios.patch('/api/todos/' + id, {})
        .then(function(response) {
          //finds the index of the todo item in the todoList
            i= thiss.todoList.findIndex(function(todo) {
               //returns the ids of the toggled todo item
            return todo.id ===response.data.id;
          });
          //the state of the todo item is updated in the todoList
          thiss.todoList[i]= response.data;
        }).catch(function(e) {
          throw e;
        });
    },
    //this method is called when the user clicks on the delete icon
    deleteTodo(id) {
      const thiss = this;
      //sends a delete request to the /api/todos endpoint in the backend to delete the todo item
      fetch('/api/todos/' +id, {
        method: "DELETE",
      }).then(function(response) {
        if (!response.ok) {
          throw new Error('Error occured in deletion'); //if the response has error, an error is thrown
        }
          i = thiss.todoList.findIndex(function(t) {
            //and returns their ids to be updated in the todolist array
          return t.id ===id;
        });
        //the todo item is deleted from the todoList
        thiss.todoList.splice(i, 1);
      }).catch(function(err) {
        throw err;
      });
    },
    //helper method
    editTodo: function (id) {
      //sets the id of the todo item to be updated
      var i =this.todoList.findIndex(function (t) {
        return t.id === id;
      });
      this.toUpdateId= id;
      //sets the value of the todo item to be updated
      this.updatedText =this.todoList[i].content;
    },
    //this method is called when the user clicks on the update icon
    updateTodo: function () {
      const thiss = this;
      //sends a put request to the /api/todos endpoint in the backend to update the todo item
      var newTodo = { id: thiss.toUpdateId, content: thiss.updatedText }; 
      //sends the id and the updated content of the todo item
      axios.put('/api/todos/' + thiss.toUpdateId, newTodo)
        .then(function (response) {
           //finds the index of the todo item in the todoList and returns it
            i = thiss.todoList.findIndex(function (t) {
            return t.id === thiss.toUpdateId;
          });
           //the todo item is updated in the todoList
          thiss.todoList.splice(i, 1, response.data);

          //the id and the content of the 'tobeupdated' todo item are set to null
          thiss.toUpdateId = null;
          thiss.updatedText = '';
        }).catch(function (err) {
          throw err;
        });
    },
     //this method is called when the user clicks on the cancel icon to cancel their update
    cancelUpdate: function () {
      this.toUpdateId = null;
      this.updatedText = '';
    } 
},
  computed: {
    //to implement pagination
    totalPages() {
      elem = this.todoList ? this.todoList.length : 0;
      return Math.max(Math.ceil(elem/5), 1);
    },
    completedItems: function() {
      if (this.hideOrShow) { //used for display and hiding of completed todos
        return this.todoList;
      } 
      else {
        return this.todoList.filter(function(t) {
          return !t.completed; //toggled case
        });
      }
    },   
    singlePage() {
      //implements pagination with 5 items per single page
      start = (this.activePage - 1) *5;
      end = start +5;
      return this.completedItems.slice(start, end); 
    }
  },
});

app.mount('#app');