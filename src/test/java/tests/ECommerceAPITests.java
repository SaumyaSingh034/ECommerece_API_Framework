package tests;

import Pojo.LoginPojo;
import Pojo.LoginPojoResponse;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.requestSpecification;

public class ECommerceAPITests {
    RequestSpecification requestSpecBuilder;
    RequestSpecification requestlogin;
    LoginPojo loginPojo = new LoginPojo();
    Response response;
    String token;
    String userId;
    String productId;

    @Test(priority = 0)
    public void doLoginCall(){
        requestSpecBuilder =  new RequestSpecBuilder().setBaseUri("https://rahulshettyacademy.com")
                .setContentType(ContentType.JSON).build();
        // Serialization
        loginPojo.setUserEmail("automationTestSaumya@gmail.com");
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
        Response response = given().spec(requestSpecBuilder).param("productName","Laptop")
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

}
