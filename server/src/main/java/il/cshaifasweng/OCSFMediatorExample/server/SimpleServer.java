package il.cshaifasweng.OCSFMediatorExample.server;

import il.cshaifasweng.OCSFMediatorExample.entities.Message;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.AbstractServer;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.ConnectionToClient;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.SubscribedClient;

import java.io.IOException;
import java.time.LocalTime;
import java.util.ArrayList;

public class SimpleServer extends AbstractServer {
	private static ArrayList<SubscribedClient> SubscribersList = new ArrayList<>();

	public SimpleServer(int port) {
		super(port);

	}

	@Override
	protected void handleMessageFromClient(Object msg, ConnectionToClient client) {
		Message message = (Message) msg;
		String request = message.getMessage();
		try {
			//we got an empty message, so we will send back an error message with the error details.
			if (request.isBlank()){
				message.setMessage("Error! we got an empty message");
				client.sendToClient(message);
			}
			//we got a request to change submitters IDs with the updated IDs at the end of the string, so we save
			// the IDs at data field in Message entity and send back to all subscribed clients a request to update
			//their IDs text fields. An example of use of observer design pattern.
			//message format: "change submitters IDs: 123456789, 987654321"
			else if(request.startsWith("change submitters IDs:")){
				message.setData(request.substring(23));
				message.setMessage("update submitters IDs");
				sendToAllClients(message);
			}
			//we got a request to add a new client as a subscriber.
			else if (request.equals("add client")){
				SubscribedClient connection = new SubscribedClient(client);
				SubscribersList.add(connection);
				message.setMessage("client added successfully");
				client.sendToClient(message);
			}
			//we got a message from client requesting to echo Hello, so we will send back to client Hello world!
			else if(request.startsWith("echo Hello")){
				message.setMessage("Hello World!");
				client.sendToClient(message);
			}
			else if(request.startsWith("send Submitters IDs")){
				message.setMessage("211924220, 315183376");
				client.sendToClient(message);

			}
			else if (request.startsWith("send Submitters")){
				message.setMessage("Julanar Hammoud, Rozaleen Hassanein");
				client.sendToClient(message);
			}
			else if (request.equals("what’s the time?")) {
				//add code here to send the time to client
				long currentTime = System.currentTimeMillis();
				String messageString = "The current time is " + currentTime;
				message.setMessage(messageString);
				client.sendToClient(message);
			}


			else if (request.startsWith("multiply")){
				//add code here to multiply 2 num
				// bers received in the message and send result back to client
				//(use substring method as shown above)
				//message format: "multiply n*m"
				String[] numbers = request.substring(9).split("\\*");
				int n = Integer.parseInt(numbers[0]);
				int m = Integer.parseInt(numbers[1]);

				// Multiply the two numbers
				int result = n * m;

				// Create the message to send back to the client
				String messageString = "The result of multiplication is: " + result;

				// Set the data property of the message object to the message string
				message.setMessage(messageString);
				client.sendToClient(message);

			}else{
				//add code here to send received message to all clients.
				//The string we received in the message is the message we will send back to all clients subscribed.
				//Example:
				// message received: "Good morning"
				// message sent: "Good morning"
				//see code for changing submitters IDs for help
				message.setData(request);
				message.setMessage(request);
				sendToAllClients(message);
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	public void sendToAllClients(Message message) {
		try {
			for (SubscribedClient SubscribedClient : SubscribersList) {
				SubscribedClient.getClient().sendToClient(message);
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

}

