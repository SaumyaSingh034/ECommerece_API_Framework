package tests;

import Pojo.LoginPojo;
import Pojo.LoginPojoResponse;
import Pojo.OrderDetail;
import Pojo.OrderRequestPojo;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.checkerframework.checker.units.qual.A;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.requestSpecification;

public class ECommerceAPITests {
    RequestSpecification requestSpecBuilder;
    RequestSpecification requestlogin;
    LoginPojo loginPojo = new LoginPojo();
    OrderRequestPojo orderRequestPojo = new OrderRequestPojo();
    OrderDetail orderDetail = new OrderDetail();
    Response response;
    String token;
    String userId;
    String productId;

    @Test(priority = 0)
    public void doLoginCall(){
        requestSpecBuilder =  new RequestSpecBuilder().setBaseUri("https://rahulshettyacademy.com")
                .setContentType(ContentType.JSON).build();
        // Serialization
        loginPojo.setUserEmail("automationTest9811@gmail.com");
        loginPojo.setUserPassword("IamQueen@000");

        requestlogin = given().log().all().spec(requestSpecBuilder).body(loginPojo);

        response = requestlogin.when()
                .post("/api/ecom/auth/login")
                .then()
                .log()
                .all().statusCode(200)
                .extract().response();

        LoginPojoResponse loginResponse = response.as(LoginPojoResponse.class);
        token = loginResponse.getToken();
        userId = loginResponse.getUserId();
        System.out.println(token);
        System.out.println(userId);
    }

    @Test(priority = 1)
    public void createProduct(){

        requestSpecBuilder = new RequestSpecBuilder().setBaseUri("https://rahulshettyacademy.com")
                .addHeader("Authorization", token)
                .build();
        response = given().spec(requestSpecBuilder).param("productName","Laptop")
                .param("productAddedBy", userId)
                .param("productCategory", "fashion")
                .param("productSubCategory", "shirts").
                param("productPrice", "11500")
                .param("productDescription", "Lenova").
                param("productFor", "men")
                .multiPart("productImage", new File("/Users/saumya.singh/IdeaProjects/ECommerceAPIFramework/src/test/java/resources/Free Baby Naming Ceremony Invitation Template (1).png"))
                .when()
                .post("/api/ecom/product/add-product")
                .then()
                .log()
                .all()
                .statusCode(201)
                .extract()
                .response();

       productId =  response.jsonPath().get("productId");
    }

    @Test(priority=2)
    public void createOrder(){




        requestSpecBuilder = new RequestSpecBuilder().setBaseUri("https://rahulshettyacademy.com")
                .addHeader("authorization",token)
                .setContentType(ContentType.JSON).build();


        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setProductOderedId(productId);
        orderDetail.setCountry("India");
        List<OrderDetail> order = new ArrayList<>();
        order.add(orderDetail);
        OrderRequestPojo opojo = new OrderRequestPojo();
        opojo.setOrder(order);

        response = given().log().all().spec(requestSpecBuilder)
                .body(opojo)
                .when()
                .post("/api/ecom/order/create-order")
                .then().log().all()
                .statusCode(200)
                .extract()
                .response();

    }

    @Test(priority = 3)
    public void deleteProduct(){
         requestSpecBuilder=	new RequestSpecBuilder().setBaseUri("https://rahulshettyacademy.com")
                .addHeader("authorization", token).setContentType(ContentType.JSON)
                .build();

        response = given().log().all().spec(requestSpecBuilder).
                pathParam("productId",productId)
                .when().delete("/api/ecom/product/delete-product/{productId}").
                then().log().all().
                extract().response();

        Assert.assertEquals("Product Deleted Successfully",response.jsonPath().get("message"));
    }

}
