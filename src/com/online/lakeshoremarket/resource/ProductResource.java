package com.online.lakeshoremarket.resource;

import java.util.ArrayList;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.online.lakeshoremarket.activity.ProductActivity;
import com.online.lakeshoremarket.representation.generic.GenericResponse;
import com.online.lakeshoremarket.representation.product.ProductRepresentation;
import com.online.lakeshoremarket.representation.product.ProductRequest;

@Path("/")
public class ProductResource {

	@GET
	@Produces({"application/xml" , "application/json"})
	@Path("/products/{searchString}")
	public ArrayList<ProductRepresentation> getProducts(@PathParam("searchString") String prodName) {
		System.out.println("GET METHOD Request for all products.............");
		ProductActivity productActivity = new ProductActivity();
		ArrayList<ProductRepresentation> prodRepresentationSet = productActivity.getProducts(prodName);
		return prodRepresentationSet;
	}
	
	@PUT
	@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	@Path("/product")
	public GenericResponse createProduct(ProductRequest prodRequest) {
		System.out.println("PUT METHOD Request for Creating a new product .............");
		GenericResponse genericResponse = new GenericResponse();
		boolean isProductCreated = false;
		ProductActivity productActivity = new ProductActivity();
		isProductCreated = productActivity.createProduct(prodRequest);
		if(isProductCreated){
			genericResponse.setMessage("Product is created");
			genericResponse.setSuccess(true);
		}else{
			genericResponse.setMessage("Product is not created");
			genericResponse.setSuccess(false);
		}
		
		return genericResponse;	
	}
	
	@GET
	@Produces({"application/xml", "application/json"})
	@Path("/product/{productName}")
	public ProductRepresentation getProduct(@PathParam("productName") String prodName) {
		System.out.println("GET METHOD Request for individual product ............" + prodName);
		ProductActivity productActivity = new ProductActivity();
		return productActivity.getProduct(prodName); 
	}
	
	@GET
	@Produces({"application/xml", "application/json"})
	@Path("/available/{productID}")
	public GenericResponse checkProductAvailability(@PathParam("productID") String prodName){ //should the path param always be ID? //is this okay? (not Product ID because there is no ID search)
		System.out.println("GET METHOD Request for Availability of Product ............" + prodName);
		boolean isProductAvailable = false;
		ProductActivity productActivity = new ProductActivity();
		isProductAvailable = productActivity.checkProductAvailability(prodName);
		GenericResponse genericResponse = new GenericResponse();
		if(isProductAvailable){
			genericResponse.setMessage("Product is available");
			genericResponse.setSuccess(true);
		}else{
			genericResponse.setMessage("Product is not available");
			genericResponse.setSuccess(false);
		}		
		return genericResponse;
	}
}
