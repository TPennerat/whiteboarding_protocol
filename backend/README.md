# Structure of the project

The project is structured in two main packages: **messages** and **objects**. They're both at the root, **fi.whiteboardaalto** (name that we gave to the base package).

- **Objects**

In the **objects** package, we have have all the classes that are related to the physical representation of an object: this is where sticky notes, drawings or images are represented.

- **Messages**

The **messages** package is subdivided into the **clients messages** (the messages that the are coming from the clients - the server therefore never sends any messages of this type) and the **server messages**.

- [ ] *Client messages (fi.whiteboardaalto.messages.client)*

All the client messages inherit from the **ActionMessage** class. In this package we will find an **object** and **session** package, which are all the classes to perform actions that are related to whether object or session (respectively). The **change** package in the action package contain the classes required to perfrom a change on an object.

- [ ] *Server messages (fi.whiteboardaalto.messages.server)*

The server messages can be ACK messages (to ack a request), error messages (to indicate an action could not be performed due to an error) or update messages (to indicate that an object state has change, that a user has joined or that a new host has been elected). This is exactly the structure of this package, where we can find the **ack**, **errors** and **updates** packages. All the classes from these packages inherit from the **Answer** class (it represents any answer - or server-side generated message - from the server).
