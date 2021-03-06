package com.online.lakeshoremarket.resource;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import com.online.lakeshoremarket.activity.OrderActivity;
import com.online.lakeshoremarket.exception.GenericLSMException;
import com.online.lakeshoremarket.representation.generic.GenericResponse;
import com.online.lakeshoremarket.representation.generic.Link;
import com.online.lakeshoremarket.representation.order.OrderRepresentation;
import com.online.lakeshoremarket.util.Constant;
import com.online.lakeshoremarket.util.LSMAuthenticator;

@Path("/")

/**
 * Provides a medium for communications between view and controller/model
 */

public class OrderResource {
	
	/**
	 * POST method to ship order
	 * @param orderIDString
	 * @param trackingNumb
	 * @param email
	 * @param password
	 * @return genericResponse (affirmative if successful; negative if not)
	 */
	
	@POST
	@Produces({"application/xml" , "application/json"})
	@Path("/order/ship")
	public GenericResponse shipOrder(OrderRepresentation custOrder, @HeaderParam("email") String email, @HeaderParam("password") String password){
		System.out.println("POST METHOD to ship order.............");
		boolean isUserAuthentic = false;
		isUserAuthentic = LSMAuthenticator.authenticateUser(email, password);
		if(isUserAuthentic){
			GenericResponse genericResponse = new GenericResponse();
			boolean isOrderShipped = false;
			OrderActivity orderActivity = new OrderActivity();
			isOrderShipped = orderActivity.shipOrder(custOrder.getOrderID(), custOrder.getTrackingNumber());
			if(isOrderShipped){
				genericResponse.setMessage("Order is shipped");
				genericResponse.setSuccess(true);
				Link cancel = new Link("Cancel Order", Constant.LSM_COMMON_URL + "/order/"+custOrder.getOrderID(), "application/xml");
				Link fulfill = new Link("Fulfill Order", Constant.LSM_COMMON_URL + "/order/fulfill", "application/xml");
				Link get = new Link("Get Order Details", Constant.LSM_COMMON_URL + "/order/" + custOrder.getOrderID(), "application/xml");
				genericResponse.setLinks(fulfill, get, cancel);
			}else{
				genericResponse.setMessage("Order is not shipped");
				genericResponse.setSuccess(false);
			}
			return genericResponse;
		}else{
			throw new GenericLSMException("User is not authorized", Response.Status.UNAUTHORIZED);
		}
	}
	
	/**
	 * POST method to fulfill order
	 * @param orderIDString
	 * @param email
	 * @param password
	 * @return (affirmative if successful; negative if not)
	 */
	
	@POST
	@Produces({"application/xml" , "application/json"})
	@Path("/order/fulfill")
	public GenericResponse fulfillOrder(OrderRepresentation custOrder, @HeaderParam("email") String email, @HeaderParam("password") String password){
		System.out.println("POST METHOD to fulfill order.............");
		boolean isUserAuthentic = false;
		isUserAuthentic = LSMAuthenticator.authenticateUser(email, password);
		if(isUserAuthentic){
			GenericResponse genericResponse = new GenericResponse();
			boolean isOrderFulfilled = false;
			OrderActivity orderActivity = new OrderActivity();
			isOrderFulfilled = orderActivity.fulfillOrder(custOrder.getOrderID());
			if(isOrderFulfilled){
				genericResponse.setMessage("Order is fulfilled");
				genericResponse.setSuccess(true);
			}else{
				genericResponse.setMessage("Order is not fulfilled");
				genericResponse.setSuccess(false);
			}
			Link get = new Link("Get Order Details", Constant.LSM_COMMON_URL + "/order/" + custOrder.getOrderID(), "application/xml");
			genericResponse.setLinks(get);	
			return genericResponse;
		}else{
			throw new GenericLSMException("User is not authorized", Response.Status.UNAUTHORIZED);
		}
	}
	
	/**
	 * DELETE method for canceling an order
	 * @param orderIDString
	 * @param email
	 * @param password
	 * @return generic response (if user is able to cancel)-- either affirmative or negative depending on status; else, exception message
	 */
	
	@DELETE
	@Produces({"application/xml" , "application/json"})
	@Path("/order/{orderIDString}")
	public GenericResponse cancelOrder(@PathParam("orderIDString") String orderIDString, @HeaderParam("email") String email, @HeaderParam("password") String password){
		System.out.println("DELETE METHOD Request for Canceling an order .............");
		boolean isUserAuthentic = false;
		isUserAuthentic = LSMAuthenticator.authenticateUser(email, password);
		if(isUserAuthentic){
			GenericResponse genericResponse = new GenericResponse();
			boolean isOrderRefunded = false;
			OrderActivity orderActivity = new OrderActivity();
			isOrderRefunded = orderActivity.cancelOrder(orderIDString);
			if(isOrderRefunded){
				genericResponse.setMessage("Order is cancelled");
				genericResponse.setSuccess(true);
				Link get = new Link("Get Order Details", Constant.LSM_COMMON_URL + "/order/" + orderIDString, "application/xml");
				genericResponse.setLinks(get);
			}else{
				genericResponse.setMessage("Order is not cancelled");
				genericResponse.setSuccess(false);
			}
			return genericResponse;
		}else{
			throw new GenericLSMException("User is not authorized", Response.Status.UNAUTHORIZED);
		}
		
	}
	
	/**
	 * GET method request for order details
	 * @param orderIDString
	 * @param email
	 * @param password
	 * @return orderRepresentation (if user is authorized to do so)
	 */
	
	@GET
	@Produces({"application/xml" , "application/json"})
	@Path("/order/{orderIDString}")
	public OrderRepresentation getOrderDetails(@PathParam("orderIDString") String orderIDString, @HeaderParam("email") String email, @HeaderParam("password") String password){
		System.out.println("GET METHOD Request for Order details.............");
		boolean isUserAuthentic = false;
		isUserAuthentic = LSMAuthenticator.authenticateUser(email, password);
		if(isUserAuthentic){
			OrderActivity orderActivity = new OrderActivity();
			OrderRepresentation orderRepresentation = new OrderRepresentation();
			orderRepresentation = orderActivity.getOrderDetails(orderIDString);
			
			Link cancel = new Link("Cancel Order", Constant.LSM_COMMON_URL + "/order/" + orderRepresentation.getOrderID(), "application/xml");
			Link ship = new Link("Ship Order", Constant.LSM_COMMON_URL + "/order/ship", "application/xml");
			Link fulfill = new Link("Fulfill Order", Constant.LSM_COMMON_URL + "/order/fulfill" , "application/xml");
			// If order is shipped, we can further proceed for fulfilling the order
			if( orderRepresentation.getOrderStatusCode() == Constant.SHIPPED )
				orderRepresentation.setLinks( cancel, fulfill );
			
			//If order is in progress, we can further proceed for shipping the order 
			if(orderRepresentation.getOrderStatusCode() == Constant.INPROGRESS )
				orderRepresentation.setLinks( cancel, ship );
			
			return orderRepresentation;
		}else{
			throw new GenericLSMException("User is not authorized", Response.Status.UNAUTHORIZED);
		}
	}
}
