docrest
=======

REST API documentation how to access REST-API


integrated with your project
============================

To use DocRest in a project please make the following changes to pom.xml:


       <dependency>
            <groupId>com.wadpam</groupId>
            <artifactId>docrest-api</artifactId>
            <version>1.23</version>
            <scope>compile</scope>
        </dependency>
        <dependency>
            <groupId>com.wadpam</groupId>
            <artifactId>docrest-doclet</artifactId>
            <version>1.23</version>
            <scope>compile</scope>
        </dependency>
        


                  <plugin>
                        <groupId>org.apache.maven.plugins</groupId>
                        <artifactId>maven-javadoc-plugin</artifactId>
                        <version>2.8</version>
                        <executions>
                            <execution>
                                <id>javadoc-docrest</id>
                                <phase>package</phase>
                                <goals>
                                    <goal>javadoc</goal>
                                </goals>
                            </execution>
                        </executions>
                       <configuration>
		                    <author>false</author>
		                    <doclet>com.wadpam.docrest.DocRestDoclet</doclet>
		                    <docletArtifact>
		                        <groupId>com.wadpam</groupId>
		                        <artifactId>docrest-doclet</artifactId>
		                        <version>${docrest.version}</version>
		                    </docletArtifact>
		                    <encoding>UTF-8</encoding>
		                    <javadocExecutable>${javadocExecutable}</javadocExecutable>
		                    <sourcepath>src/main/java</sourcepath>
		                    <useStandardDocletOptions>false</useStandardDocletOptions>
		                    <verbose>true</verbose>
		                    <includeDependencySources>true</includeDependencySources>
		                    <includeTransitiveDependencySources>true</includeTransitiveDependencySources>
		                    <dependencySourceIncludes>
		                    	<dependencySourceInclude>com.wadpam.openserver:*</dependencySourceInclude>
		                    </dependencySourceIncludes>
                	</configuration>
                    </plugin>
                    

How to use rest-doclet annotation
==================================
@RestReturn , @RestCode


    /**
     * <pre>
     * get my dashboard view 
     * 
     * -List of Top  latest Offers
     * - List of Categories
     * </pre>
     * 
     * 
     * @param brand set to true to get inner brand objects. optional true/false, default value is false. boolean
     * @param favorite set to true if you want favorite is populated, default value false. boolean
     * @param ratings set to true to get rating results included - default value false . boolean
     * @param request 
     * 
     * 
     */
    @RestReturn(value=void.class, code={
        @RestCode(code=200, description="OK", message="OK")
    })
    @RequestMapping(value="dashboard", method=RequestMethod.GET)
    @ResponseBody
    public ResponseEntity myDashboard(HttpServletRequest request,
            @RequestParam(value = "brand", defaultValue = "false") boolean brand,
            @RequestParam(value = "favorite", defaultValue = "false") boolean favorite,
            @RequestParam(value = "ratings", defaultValue = "false") boolean ratings
            ) {

        final Map<String,Object> body = service.myDashboard(10, WebHelper.getUserId(request), brand, favorite, ratings); 

        return new ResponseEntity(body, HttpStatus.OK);
    }
    


 Generate rest api documentation
 ====================================

 mvn javadoc:javadoc -Pjavadoc
 
 Tip
 =====
 - @RequestParam(required=false) - Please add 'required' to every RequestParam. It produces better DocRest. Value should be true or false.
 
 - Optional readme-docrest.txt file directly under project directory will be used as top guideline of REST API
       
https://travis-ci.org/sophea/docrest.svg?branch=master
