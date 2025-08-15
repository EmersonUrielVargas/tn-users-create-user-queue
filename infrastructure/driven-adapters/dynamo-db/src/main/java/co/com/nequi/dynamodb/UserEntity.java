package co.com.nequi.dynamodb;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*;

@DynamoDbBean
public class UserEntity {

    private Long id;
    private String name;
    private String lastname;
    private String email;
    private String photoUrl;


    public UserEntity() {
    }

    public UserEntity(Long id, String name, String email, String photoUrl, String lastname) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.photoUrl = photoUrl;
        this.lastname = lastname;
    }

    @DynamoDbPartitionKey
    @DynamoDbAttribute("id")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @DynamoDbAttribute("name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @DynamoDbAttribute("lastname")
    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    @DynamoDbAttribute("email")
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @DynamoDbAttribute("photoUrl")
    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

}
