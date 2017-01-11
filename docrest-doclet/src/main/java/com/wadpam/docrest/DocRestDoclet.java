/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wadpam.docrest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.javadoc.AnnotationDesc;
import com.sun.javadoc.AnnotationDesc.ElementValuePair;
import com.sun.javadoc.AnnotationTypeDoc;
import com.sun.javadoc.AnnotationTypeElementDoc;
import com.sun.javadoc.AnnotationValue;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.ParamTag;
import com.sun.javadoc.Parameter;
import com.sun.javadoc.RootDoc;
import com.wadpam.docrest.domain.Method;
import com.wadpam.docrest.domain.Param;
import com.wadpam.docrest.domain.Resource;
import com.wadpam.docrest.domain.RestReturn;
import com.wadpam.docrest.test.AuthorizationTest;

/**
 * @author os + sm
 */
public class DocRestDoclet {

    protected final VelocityContext vc = new VelocityContext();
    private static final Logger LOG = Logger.getLogger(DocRestDoclet.class.getName());
    private static final String INDENT = "&nbsp;&nbsp;&nbsp;";
    private static RootDoc rootDoc;
    private static final Map<String, ClassDoc> classDocs = new HashMap<String, ClassDoc>();
    /** for JSON Object classes */
    private static final Map<String, Class> jsonClassMap = new TreeMap<String, Class>();
    private static final Map<String, ClassDoc> jsonDocMap = new TreeMap<String, ClassDoc>();
    private static final Map<String, ClassDoc> jsonDocMapExtra = new TreeMap<String, ClassDoc>();

    protected static void addMethodsRecursive(Set<MethodDoc> methods, ClassDoc classDoc, ClassDoc child) {
        // LOG.info("+++ addMethods for " + classDoc.name());

        if (!Object.class.getSimpleName().equals(classDoc.name())) {

            if (isInScopeOf(classDocs, classDoc.qualifiedName())) {
                try {
                    Class c = Class.forName(classDoc.name());
                    // if(!Modifier.isAbstract(c.getModifiers())){
                    jsonClassMap.put(classDoc.qualifiedName(), c);
                    jsonDocMap.put(classDoc.qualifiedName(), classDoc);
                    // }
                } catch (ClassNotFoundException e) {

                }

            }

            if (null != classDoc.superclass()) {
                addMethodsRecursive(methods, classDoc.superclass(), classDoc);
            }
            for (MethodDoc m : classDoc.methods()) {
                // LOG.info("------------classDoc " + classDoc.qualifiedName());
                if (isGetter(m.name())) {
                    // try{
                    // if(!isClassIgnoreProperty(child.qualifiedName(),
                    // getMemberName(m.name()))){
                    methods.add(m);
                    // }
                    // }catch(ClassNotFoundException exc){

                    // }

                }
            }
        }
    }

    /**
     * Not primitive or String, we need to know more its members
     */
    protected static void appendJsonMembers(StringBuffer sb, String entityName, String indent, Class child)
            throws ClassNotFoundException {
        Class c = Class.forName(entityName);
        // LOG.info("  Producing JSON/HTML for " + c);
        // recursion with it parents
        if (null != c.getSuperclass() && !Object.class.equals(c.getSuperclass())) {
            appendJsonMembers(sb, c.getSuperclass().getName(), indent, c);
        }

        // Should show it as menus or navigations too
        // listed at the very top of the page under JSON objects.
        // jsonClassMap.put(entityName, c);
        ClassDoc classDoc = rootDoc.classNamed(entityName);
        if (isInScopeOf(classDocs, entityName)) {
            jsonClassMap.put(entityName, c);
            jsonDocMap.put(entityName, classDoc);
        }

        Field fields[] = c.getDeclaredFields();

        Set<String> ignoreProperties = child != null ? getClassIgnoreProperties(child) : new HashSet<String>();
        LOG.info("Ignored properties = " + ignoreProperties);

        Arrays.sort(fields, new Comparator<Field>() {
            // sort attribute by name
            @Override
            public int compare(Field o1, Field o2) {
                return o1.getName().compareToIgnoreCase(o2.getName());
            }

        });
        // List all of attributes of the entity
        for (int i = 0; i < fields.length; i++) {
            //ignore
            
            // exclude static field
            if (!isStaticField(fields[i])) {

                // @JsonIgnore excluded and @JsonIgnorProperies excluded
                if (!isJsonIgnoreFiled(fields[i])  ) {
                    if (!ignoreProperties.contains(fields[i].getName())) {
                        
                        String fieldNameValue = String.format("<b>\"%s\"</b>:", fields[i].getName());
                        if (sb.toString().contains(fieldNameValue)) {
                            continue;
                        }
                        sb.append("<div>");
                        sb.append(indent);
                        sb.append(INDENT);
                        sb.append(fieldNameValue);
                        String paramClass = fields[i].getType().getName();
                        String genericType = "?";

                        genericType = getGenericType(fields[i]);

                        // limit scope to detail, within root docs only, custom
                        // data type in this project
                        ClassDoc p = classDocs.get(paramClass);
                        //If type domain is T , and element is selfReference Type, in this case just display Object
                        // see Content.java ( filed Content prooduct)
                        if (null != p && !entityName.equals(p.qualifiedName())) {
                            //LOG.info(" P : " + p.qualifiedName()  + "  , entityName : " + entityName);
                            sb.append(getJson(paramClass, paramClass, indent + INDENT));
                        } else {
                            // collection
                            if (Collection.class.getName().startsWith(paramClass)) {
                                // outside scope, no need to give more detail,
                                // it should be a standard type
                                sb.append(Collection.class.getSimpleName() + "&lt;" + getTypeQualifiedName(genericType) + "&gt;");
                            }
                            // List
                            else if (List.class.getName().startsWith(paramClass)) {
                                sb.append(List.class.getSimpleName() + "&lt;" + getTypeQualifiedName(genericType) + "&gt;");
                            } else {
                                // outside scope, no need to give more detail,
                                // it should be a standard type
                                sb.append(fields[i].getType().getSimpleName());
                            }
                        }
                        if (i < fields.length - 1) {
                            sb.append(',');
                        }
                        sb.append("</div>");
                    }
                }
            }
        }
    }

    protected static String getTypeQualifiedName(String className) {

        String[] part = className.split("\\.");
        if (part.length > 0) {
            //return part[part.length - 1];
            return String.format("<a href=\"api.html#%s\" class=\"link\">%s</a>", className.replace("class ", ""), part[part.length - 1]);
        }
        return "";
    }

    protected static String getGenericType(Field field) {

        String genericType = "";
        try {
            ParameterizedType type = (ParameterizedType) field.getGenericType();
            genericType = type.getActualTypeArguments()[0].toString();
            // LOG.info("++++++++++++++++++parameter->" + genericType);
        } catch (ClassCastException exc) {
            // Do nothing
        }
        return genericType;
    }

    protected static String getComment(MethodDoc methodDoc, String parameterName) {
        for (ParamTag p : methodDoc.paramTags()) {
            if (parameterName.equals(p.parameterName())) {
                return p.parameterComment();
            }
        }
        return "";
    }

    protected static Iterable<MethodDoc> getInheritedMethods(ClassDoc classDoc) {
        final Set<MethodDoc> methods = new TreeSet<MethodDoc>(new Comparator<MethodDoc>() {

            @Override
            public int compare(MethodDoc o1, MethodDoc o2) {
                return getMemberName(o1.name()).compareToIgnoreCase(getMemberName(o2.name()));
            }
        });

        addMethodsRecursive(methods, classDoc, null);

        return methods;
    }

    protected static String getJson(String className, String entityName) {
        return getJson(className, entityName, "");
    }

    /**
     * Generate JSON string format to display on HTML page
     */
    protected static String getJson(String className, String entityName, String indent) {
        final StringBuffer sb = new StringBuffer();
        // Iterable
        if (className.startsWith(List.class.getName()) || className.endsWith("]")) {
            className = List.class.getName();
            sb.append('[');
        } else if (className.startsWith(Collection.class.getName())) {
            className = Collection.class.getName();
            sb.append('[');
        } else if (null == entityName) {
            // default value of entity
            entityName = className;
        }

        if (null != entityName) {
            LOG.info(">>>>entityName : " + entityName);
            ClassDoc classDoc = rootDoc.classNamed(entityName);
            // LOG.info("JSON lookup " + className + "<" + entityName +
            // ">  gives " + classDoc);

            if (null == classDoc) {
                return entityName;
            }

            // JSON objects at the bottom of the page
            // jsonDocMap.put(entityName, classDoc);
            if (isInScopeOf(classDocs, entityName)) {
                jsonDocMap.put(entityName, classDoc);
            }

            // should be JSON's basic types
            if (String.class.getName().equals(entityName)) {
                sb.append(String.class.getSimpleName());
            } else if (Long.class.getName().equals(entityName)) {
                sb.append(Long.class.getSimpleName());
            } else if (Integer.class.getName().equals(entityName)) {
                sb.append(Integer.class.getSimpleName());
            } else if (Float.class.getName().equals(entityName)) {
                sb.append(Float.class.getSimpleName());
            } else if (Double.class.getName().equals(entityName)) {
                sb.append(Double.class.getSimpleName());
            } else if (Date.class.getName().equals(entityName)) {
                sb.append(Date.class.getSimpleName());
            } else if (Void.class.getName().equals(entityName)) {
                sb.append(Void.class.getSimpleName());
            } else {

                sb.append("{");
                try {
                    // display deeper, more detail, not only simple name here
                    appendJsonMembers(sb, entityName, indent, null);
                } catch (ClassNotFoundException e) {

                    // Class c = Class.forName(entityName);
                    // Java's Extension Directories is kept in Java's System
                    // Property "java.ext.dirs".

                    // Example
                    // com.sma.offers.json.JBrand
                    // java.lang.ClassNotFoundException:
                    // com.sma.offers.json.JBrand
                    // LOG.info(e.toString());

                    // Alternative, same entity
                    // List all of attributes of the entity, all getter methods
                    // (similar to attributes) from classDoc

                    // Should show it as menus or navigations too
                    // listed at the very top of the page under JSON objects.
                    // Class c = Class.forName(entityName);
                    if (isInScopeOf(classDocs, entityName)) {
                        jsonClassMap.put(entityName, null);
                    }

                    for (MethodDoc method : getInheritedMethods(classDoc)) {

                        if (isGetter(method.name())) {

                            sb.append("<div>");
                            sb.append(indent);
                            sb.append(INDENT);
                            sb.append("<b>\"");
                            sb.append(getMemberName(method.name()));
                            sb.append("\"</b>&nbsp;:&nbsp;");
                            String paramClass = method.returnType().qualifiedTypeName();
                            // ClassDoc p = classDocs.get(paramClass);
                            // LOG.info(String.format("    -- for return attribute \"%s\" : %s the classDoc is "
                            // + p,
                            // getMemberName(method.name()), paramClass));
                            // if (null != p) {
                            sb.append(getJson(paramClass, paramClass, indent + INDENT));
                            // }
                            // else {
                            // sb.append(paramClass);
                            // }
                            sb.append(',');
                            sb.append("</div>");
                        }
                    }
                }
                sb.append(indent);
                sb.append("}");
            }
        }

        // Iterable
        if (List.class.getName().equals(className) || Collection.class.getName().equals(className)) {
            sb.append(",<div>");
            sb.append(INDENT);
            sb.append("...</div>");
            sb.append(INDENT);
            sb.append("]");
        }

        return sb.toString();
    }

    protected static boolean isInScopeOf(Map<String, ClassDoc> classMaps, String qualifiedName) {
        // by default, it should be classDocs
        if (null == classMaps) {
            classMaps = classDocs;
            
        }
        return null != classMaps.get(qualifiedName);
    }

    public static List<MethodDoc> getMethods(ClassDoc classDoc) {

        List<MethodDoc> listMethods = new ArrayList<MethodDoc>();
        Iterable<MethodDoc> methods = getInheritedMethods(classDoc);

        for (MethodDoc method : methods) {
            listMethods.add(method);
        }

        return listMethods;

    }

    public static List<MethodDoc> getParentMethods(ClassDoc classDoc) {
        return classDoc.superclass() !=null ? getMethods(classDoc.superclass()) : null;
    }

    public static String getFieldComment(ClassDoc classDoc, String field) {

        if (!Object.class.getSimpleName().equals(classDoc.name())) {

            FieldDoc[] fields = classDoc.fields(false);
            // LOG.info(">>> field namme" + classDoc.superclass().name());
            for (FieldDoc f : fields) {
                if (f.name().equals(field)) {
                    return f.commentText();
                }
            }

            return getFieldComment(classDoc.superclass(), field);
        }

        return "";
    }

    public static String getMemberName(String methodName) {
        int beginIndex = methodName.startsWith("get") ? 3 : 2;
        StringBuffer sb = new StringBuffer();
        if (beginIndex + 1 < methodName.length()) {
            sb.append(methodName.substring(beginIndex, beginIndex + 1).toLowerCase());
            sb.append(methodName.substring(beginIndex + 1));
        }
        return sb.toString();
    }

    public String getReturnType(String className, MethodDoc methodDoc) {
        String methodName = methodDoc.name();
        // LOG.info("-------------- Method name: " + methodName);
        try {
//            LOG.info(this.getClass().getClassLoader());
            Class c = Class.forName(className);

            // build parameter class list

            java.lang.reflect.Method rm = c.getMethod(methodName);
            // LOG.info("  -> genericReturnType " + rm.getGenericReturnType());
            if (rm.getGenericReturnType() instanceof ParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType) rm.getGenericReturnType();
                // LOG.info("     parameterizedType " +
                // parameterizedType.toString());
                java.lang.reflect.Type argTypes[] = parameterizedType.getActualTypeArguments();
                if (1 == argTypes.length) {
                    String returnValue = argTypes[0].toString();
                    if (returnValue.startsWith("class ")) {
                        return returnValue.substring("class ".length());
                    }
                    return returnValue;
                }
            }
            return rm.getGenericReturnType().toString();
        } catch (ClassNotFoundException e) {
            LOG.warning("getReturnType ClassNotFound " + className + " " + e.getMessage());
            e.printStackTrace();
            return className;
        } catch (NoSuchMethodException e) {
            LOG.warning("getReturnType NoSuchMethod " + methodName + " " + e.getMessage());
            e.printStackTrace();
        }
        return "";
    }

    
    public static String getReturnType(ClassDoc classDoc, MethodDoc methodDoc) {
        String methodName = methodDoc.name();
        // LOG.info("-------------- Method name: " + methodName);
        try {
            Class c = Class.forName(classDoc.qualifiedName());

            // build parameter class list
            List<Class> paramClasses = new ArrayList<Class>();
            int i = 0;
            for (Parameter p : methodDoc.parameters()) {
                String className = p.type().qualifiedTypeName();
                try {
                    paramClasses.add(Class.forName(className));
                } catch (ClassNotFoundException skipSimple) {

                }
            }

            java.lang.reflect.Method rm = c.getMethod(methodName, paramClasses.toArray(new Class[0]));
            // LOG.info("  -> genericReturnType " + rm.getGenericReturnType());
            if (rm.getGenericReturnType() instanceof ParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType) rm.getGenericReturnType();
                // LOG.info("     parameterizedType " +
                // parameterizedType.toString());
                java.lang.reflect.Type argTypes[] = parameterizedType.getActualTypeArguments();
                if (1 == argTypes.length) {
                    String returnValue = argTypes[0].toString();
                    if (returnValue.startsWith("class ")) {
                        return returnValue.substring("class ".length());
                    }
                    return returnValue;
                }
            }
            return rm.getGenericReturnType().toString();
        } catch (ClassNotFoundException e) {
            LOG.warning("getReturnType ClassNotFound " + classDoc.qualifiedName() + " " + e.getMessage());
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            LOG.warning("getReturnType NoSuchMethod " + methodName + " " + e.getMessage());
            e.printStackTrace();
        }
        return "";
    }

    protected static String[] getValue(AnnotationDesc annotationDesc, String name) {
        String[] returnValue = new String[1];
        returnValue[0] = "";
        AnnotationTypeElementDoc element;
        AnnotationValue value;
        for (ElementValuePair evp : annotationDesc.elementValues()) {
            element = evp.element();
            value = evp.value();
            if (name.equals(element.name())) {
                Object[] values = (Object[]) value.value();
                returnValue = new String[values.length];
                int i = 0;
                for (Object v : values) {
                    StringBuffer sb = new StringBuffer(v.toString());
                    // trim leading and tailing quotes
                    if (0 == sb.indexOf("\"")) {
                        sb.deleteCharAt(0);
                    }
                    if (sb.length() - 1 == sb.lastIndexOf("\"")) {
                        sb.deleteCharAt(sb.length() - 1);
                    }

                    if (0 == sb.indexOf("/")) {
                        sb.deleteCharAt(0);
                    }

                    returnValue[i++] = sb.toString();
                }
            }
        }
        return returnValue;
    }

    protected static Object getValueAsObject(AnnotationDesc annotationDesc, String name) {
        AnnotationTypeElementDoc element;
        AnnotationValue value;
        for (ElementValuePair evp : annotationDesc.elementValues()) {
            element = evp.element();
            value = evp.value();
            if (name.equals(element.name())) {
                return value.value();
            }
        }
        return null;
    }

    public static boolean isGetter(String methodName) {
        return null != methodName && (methodName.startsWith("get") || methodName.startsWith("is"));
    }

    public static boolean isStaticField(Field field) {
        // LOG.info(">>>>>>> field" + field.getName() + " is " +
        // java.lang.reflect.Modifier.isStatic(field.getModifiers
        // ()));
        return java.lang.reflect.Modifier.isStatic(field.getModifiers());
    }

    public static boolean isRequiredParam(Method method) {
        List<Param> params = method.getParameters();
        for (Param param : params) {
            return param.isRequired();
        }
        return false;
    }

    public static boolean isJsonIgnoreFiled(Field field) {

        boolean isJsonIgnore = false;

        for (Annotation annotation : field.getAnnotations()) {
            LOG.info("Found annotation " + annotation.annotationType() + " in field " + field);
            // Check whether this annotation is JsonIgnore of jackson from
            // codehaus or fasterxml
            if ("JsonIgnore".equals(annotation.annotationType().getSimpleName())
                    | annotation.annotationType().getPackage().getName().contains("jackson")) {
                isJsonIgnore = true;
                break;
            }
        }

        return isJsonIgnore;
    }

    public static Set<String> getClassIgnoreProperties(Class classType) {
        Annotation annotations[] = classType.getAnnotations();
        Set<String> result = new HashSet<String>();
        for (Annotation annotation : annotations) {
            LOG.info("Found annotation " + annotation.annotationType() + " in class " + classType);
            // Check whether this annotation is JsonIgnoreProperties of jackson
            // from codehaus or fasterxml
            if ("JsonIgnoreProperties".equals(annotation.annotationType().getSimpleName())
                    && annotation.annotationType().getPackage().getName().contains("jackson")) {
                String[] values = getAnnotationMethodStringArrayValue(annotation);
                if (values != null) {
                    result.addAll(Arrays.asList(values));
                }
            }
        }
        return result;
    }

    private static String[] getAnnotationMethodStringArrayValue(final Annotation annotation) {
        String[] result = null;
        try {
            java.lang.reflect.Method valueMethod = annotation.annotationType().getMethod("value");
            Class<?> type = valueMethod.getReturnType();
            if (type.isArray() && String.class.equals(type.getComponentType())) {
                result = (String[]) valueMethod.invoke(annotation);
                LOG.info("Result list of values = " + Arrays.asList(result));
            }
        } catch (Exception e) {
            LOG.log(Level.WARNING, "Unable to read @JsonIgnoreProperties annotation field value.", e);
        }
        return result;
    }

  //use by template velocity template
    public static boolean isJsonIgnoreFiledsWithParentClass(String entityName, String property) {
        try {
            
            Class c = Class.forName(entityName);
            Set<String> ignoredProperties = getClassIgnoreProperties(c);
            if (ignoredProperties.contains(property)) {
                return true;
            }
            //
            Field fields[] = c.getSuperclass().getDeclaredFields();
            for (Field field : fields) {
                if (field.getName().equals(property) && isJsonIgnoreFiled(field)) {
                    return true;
                }
            }
        } catch (ClassNotFoundException exc) {
            // Do nothing
        }
        return false;
    }
    
    public static boolean isJsonIgnoreFiled(String entityName, String property) {
        try {
            Class c = Class.forName(entityName);
            Set<String> ignoredProperties = getClassIgnoreProperties(c);
            if (ignoredProperties.contains(property)) {
                return true;
            }
            Field fields[] = c.getDeclaredFields();
            for (Field field : fields) {
                if (isJsonIgnoreFiled(field) && field.getName().equals(property)) {
                    return true;
                }
            }
        } catch (ClassNotFoundException exc) {
            // Do nothing
        }
        return false;
    }

    public static int optionLength(String option) {
        if ("-basePath".equals(option)) {
            return 2;
        } else if ("-baseUrl".equals(option)) {
            return 2;
        } else if ("-clientId".equals(option)) {
            return 2;
        } else if ("-appName".equals(option) || "-appVersion".equals(option)) {
            return 2;
        } else if ("-plugins".equals(option)) {
            return 2;
        } else if ("-d".equals(option)) {
            return 2;
        } else if ("-doctitle".equals(option)) {
            // https://discuss.gradle.org/t/javadoc-task-passes-standard-doclet-options-to-javadoc-command-even-when-a-custom-doclet-is-specified/870
            return 2;   // the -doctitle option is ignored, and is here to deal with a gradle bug
                        // that always passes it to javadoc, even if a custom doclet is used
        } else if ("-windowtitle".equals(option)) {
            return 2;   // the -windowtitle option is ignored, and is here to deal with a gradle bug
                        // that always passes it to javadoc, even if a custom doclet is used
        }

        return 0;
    }

    protected static void setRootDoc(RootDoc rootDoc) {
        DocRestDoclet.rootDoc = rootDoc;
    }

    protected static String getReadme() {

        String fileName = "";
        StringBuilder contents = new StringBuilder();
        String line;
        BufferedReader bReader = null;

        // cross platforms
        String[] pwd = System.getProperty("user.dir").split("/target/");
        if (pwd.length > 0) {
            fileName = pwd[0] + "/readme-docrest.txt";
        }
        
        try {
            bReader = new BufferedReader(new FileReader(new File(fileName)));
            while ((line = bReader.readLine()) != null) {
                contents.append(line).append(System.lineSeparator());
            }
        } catch (FileNotFoundException e) {
            // e.printStackTrace();
            LOG.warning("readme-docrest.txt file directly under project directory will be used as top guideline of REST "
                    + "API.");
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            try {
                if (bReader != null) {
                    bReader.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        return contents.toString();
    }

    public static boolean start(RootDoc root) {

        try {
            DocRestDoclet doclet = new DocRestDoclet();
            Collection<String> plugins = new ArrayList<String>();
            for (String[] options : root.options()) {
                if ("-basePath".equals(options[0])) {
                    doclet.setBasePath(options[1]);
                } else if ("-baseUrl".equals(options[0])) {
                    doclet.setBaseUrl(options[1]);
                } else if ("-clientId".equals(options[0])) {
                    doclet.setClientId(options[1]);
                } else if ("-appName".equals(options[0])) {
                    doclet.setAppName(options[1]);
                } else if ("-appVersion".equals(options[0])) {
                    doclet.setAppVersion(options[1]);
                } else if ("-plugins".equals(options[0])) {
                    plugins.add(options[1]);
                } else if ("-d".equals(options[0])) {
                    doclet.setDestinationFolder(options[1]);
                    LOG.info("Writing report to destination folder: " + doclet.getDestinationFolder());
                }
            }

            doclet.addAttribute("root", root);
            Collection<Resource> resources = doclet.traverse(root);

            doclet.merge("api_html.vm", doclet.getDestinationFolder(), "api.html");

            int count = 0;

            for (Resource r : resources) {
                // Logger.getLogger(DocRestDoclet.class.getName()).log(Level.SEVERE,
                // null, "yo");
                r.setCount(count);

                doclet.merge("api_resource2.vm", doclet.getDestinationFolder(), r);
                count++;
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(DocRestDoclet.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(DocRestDoclet.class.getName()).log(Level.SEVERE, null, ex);
        }

        return true;
    }

    /**
     * @param st
     *            "400 = C, 300 = B, 200 = A"
     * @return "200 = A, 300 = B, 400 = C"
     */
    public static String sortString(String st) {
        String[] stArray = st.split(","); // stArray[0] = "400 = C", stArray[1]
                                          // = "300 = B", .....
        for (int i = 0; i < stArray.length; i++) {
            stArray[i] = stArray[i].trim();
        }
        Arrays.sort(stArray);
        StringBuilder sb = new StringBuilder();
        for (String str : stArray) {
            sb.append(str).append(",");
        }
        if (-1 != sb.lastIndexOf(",")) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    /*
     * https://warburtons-test.appspot.com/oauth/wbt/authorize?client_id=localhost
     * .generic-app&redirect_uri=/ugly/%23providerId=gekko &response_type=token
     */
    private String basePath;

    private String baseUrl;

    private String clientId;

    /** The application name we are generating documentation for */
    private String appName;

    /** The application name we are generating documentation for */
    private String appVersion;

    /** The folder in which we will output the docrest report files */
    private String destinationFolder;

    public DocRestDoclet() throws Exception {
        final Properties p = new Properties();
        p.setProperty("class.resource.loader.description", "Velocity Classpath Resource Loader");
        p.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");

        p.setProperty("resource.loader", "class");
        Velocity.init(p);
    }

    protected void addAttribute(String name, Object value) {
        vc.put(name, value);
    }

    public String getBasePath() {
        return basePath;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public String getClientId() {
        return clientId;
    }

    public String getAppName() {
        return appName;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public String getDestinationFolder() {
        return destinationFolder;
    }

    protected void merge(String templateFilename, String folder, String javaFilename) throws FileNotFoundException, IOException {
        final File javaFile = (null != folder) ? new File(folder, javaFilename) : new File(javaFilename);

        // create destination folder?
        File destinationFolder = javaFile.getParentFile();
        if (null != destinationFolder && false == destinationFolder.exists()) {
            destinationFolder.mkdirs();
        }
        
        // copy css file
        final InputStream cssIn = getClass().getResourceAsStream("/api.css");
        final File cssFile = new File(destinationFolder, "api.css");
        final FileOutputStream cssOut = new FileOutputStream(cssFile);
        byte b[] = new byte[1024];
        int count;
        while (0 < (count = cssIn.read(b))) {
            cssOut.write(b, 0, count);
        }
        cssOut.close();
        cssIn.close();
         
        //copy api-tool-populator.js
        final InputStream apiToolPopulatorIn = getClass().getResourceAsStream("/api-tool-populator.js");
        final File apiToolPopulatorJsFile = new File(destinationFolder, "api-tool-populator.js");
        final FileOutputStream apiToolPopulatorOut = new FileOutputStream(apiToolPopulatorJsFile);
        while (0 < (count = apiToolPopulatorIn.read(b))) {
            apiToolPopulatorOut.write(b, 0, count);
        }
        
        apiToolPopulatorOut.close();
        apiToolPopulatorIn.close();
        
        
        //copy api-tool.js
        final InputStream apiToolIn = getClass().getResourceAsStream("/api-tool.js");
        final File apiToolJsFile = new File(destinationFolder, "api-tool.js");
        final FileOutputStream apiToolOut = new FileOutputStream(apiToolJsFile);
        while (0 < (count = apiToolIn.read(b))) {
            apiToolOut.write(b, 0, count);
        }
        apiToolOut.close();
        apiToolIn.close();
        
        
        final PrintWriter writer = new PrintWriter(javaFile);
        Template template;
        try {
            template = Velocity.getTemplate(templateFilename);
            template.merge(vc, writer);
        } catch (ResourceNotFoundException ex) {
            Logger.getLogger(DocRestDoclet.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseErrorException ex) {
            Logger.getLogger(DocRestDoclet.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(DocRestDoclet.class.getName()).log(Level.SEVERE, null, ex);
        }
        writer.close();
    }

    protected void merge(String templateFilename, String folder, Resource res) throws FileNotFoundException, IOException {
        String javaFilename = res.getSimpleType() + ".json";
        final File javaFile = (null != folder) ? new File(folder, javaFilename) : new File(javaFilename);

        final PrintWriter writer = new PrintWriter(javaFile);
        Template template;
        try {
            template = Velocity.getTemplate(templateFilename);
            vc.put("resource", res);
            template.merge(vc, writer);
        } catch (ResourceNotFoundException ex) {
            Logger.getLogger(DocRestDoclet.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseErrorException ex) {
            Logger.getLogger(DocRestDoclet.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(DocRestDoclet.class.getName()).log(Level.SEVERE, null, ex);
        }
        writer.close();
    }

    public String renderAnchor(String canonicalName) {
        if (null == canonicalName) {
            return "";
        }
        return canonicalName.replaceAll("[\\.\\/\\{\\}\\(\\)]", "").toLowerCase();
    }
    
    public String renderType(String memberType) {
        String returnValue = memberType;
        
        for (String t : jsonDocMap.keySet()) {

            String findString = t;

            int diff = memberType.length() - findString.length() - memberType.indexOf(findString);
            if (diff == 0 || (diff == 4 && memberType.indexOf("&gt;") > -1)) {
                returnValue = returnValue.replace(t, String.format("<a href=\"api.html#%s\" class=\"link\">%s</a>", t, t));
            }

        }

        return returnValue;
    }
    public Map<String, ClassDoc> getJsonDocMapExtra() {
        return jsonDocMapExtra;
    }
    /**renderTypeWithDetailObjectExtraJson*/
    public String renderTypeWithDetailObjectExtraJson(String memberType, String className, MethodDoc methodDoc) {
        String returnValue = memberType;
       
        List<String> ignoreList = Arrays.asList("java.util.List","java.util.Collection", "java.util.ArrayList");
        if (ignoreList.contains(returnValue)) {
            final String deailsType = getReturnType(className, methodDoc);
            returnValue = String.format("%s<<a href=\"api.html#%s\" class=\"link\">%s</a>>",returnValue, deailsType, deailsType);
        } else if (isGekkoOrDmiPackage(returnValue)) {
            returnValue = String.format("<a href=\"api.html#%s\" class=\"link\">%s</a>",returnValue, returnValue);
        }

        return returnValue;
    }
    
    /**renderTypeWithDetailObjectExtraJson => alternative method*/
    public String renderTypeWithDetailObjectExtraJson2(String memberType, String className, MethodDoc methodDoc) {
        String returnValue = memberType;
        
        List<String> ignoreList = Arrays.asList("java.util.List","java.util.Collection", "java.util.ArrayList");
        if (ignoreList.contains(returnValue)) {
            final String deailsType = getReturnType(className, methodDoc);
            returnValue = String.format("%s", deailsType);
        }else{
            returnValue = "";
        }
        return returnValue;
    }
    
    private boolean isGekkoOrDmiPackage(String type) {
        return type != null;
    }
    public String renderTypeWithDetailObject(String memberType, String className, MethodDoc methodDoc) {
        String returnValue = memberType;
        for (String t : jsonDocMap.keySet()) {

            // examples:

            String findString = t;

            int diff = memberType.length() - findString.length() - memberType.indexOf(findString);
            if (diff == 0 || (diff == 4 && memberType.indexOf("&gt;") > -1)) {
                returnValue = returnValue.replace(t, String.format("<a href=\"api.html#%s\" class=\"link\">%s</a>", t, t));
            }

        }
        List<String> ignoreList = Arrays.asList("java.util.List","java.util.Collection", "java.util.ArrayList");
        if (ignoreList.contains(returnValue)) {
            final String detailsType = getReturnType(className, methodDoc);
            if (isGekkoOrDmiPackage(detailsType)) {
                try {
                    Class c = Class.forName(detailsType);
                    if (!jsonDocMap.containsKey(detailsType)) {
                        LOG.info("<<<<<<<<<<<<<<" + detailsType);
                        ClassDoc classDoc = rootDoc.classNamed(c.getName());
                        //classDocs.containsKey(deailsType); 
                        jsonDocMapExtra.put(detailsType, classDoc);
                    }
                    
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
            
            returnValue = String.format("%s<<a href=\"api.html#%s\" class=\"link\">%s</a>>",returnValue, detailsType, detailsType);
        }

        return returnValue;
    }

    
    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public void setDestinationFolder(String destinationFolder) {
        this.destinationFolder = destinationFolder;
    }
    public String getEndpoint (String readme) {
       
       if (readme.indexOf("[") == -1 || readme.indexOf("[") == -1 ) {
           return null;
       }
       
       String serverUrls =  readme.substring(readme.indexOf("["), readme.indexOf("]") + 1 );
       
       try {
            List<String> list = new ObjectMapper().readValue(serverUrls, ArrayList.class);
            LOG.info(String.format("============================ host name %s", getDomainName(list.get(0))));
            return getDomainName(list.get(0));
       }
       catch (JsonParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
       }
       catch (JsonMappingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
       }
       catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
       }
       catch (URISyntaxException e) {
           // TODO Auto-generated catch block
           e.printStackTrace();
       }
       return null;
    }
    
    public static String getDomainName(String url) throws URISyntaxException {
        URI uri = new URI(url);
        String domain = uri.getHost();
        return domain.startsWith("www.") ? domain.substring(4) : domain;
    }
    
    public String getReadmeDescription (String readme) {
        if ( readme.indexOf("<pre>") == -1 || readme.indexOf("</pre>") == -1) {
            return null;
        }    
        String newStr =  readme.substring(readme.indexOf("<pre>"), readme.lastIndexOf("</pre>") + 6 );
        return new StringEscapeUtils().escapeJava(((newStr)));
     }
    protected Collection<Resource> traverse(RootDoc root) {
        this.rootDoc = root;
        vc.put("basePath", getBasePath());
        vc.put("baseUrl", getEndpoint(getReadme()));
        vc.put("description", getReadmeDescription(getReadme()));
        vc.put("clientId", getClientId());
        vc.put("appName", getAppName());
        vc.put("appVersion", getAppVersion());
        Format formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        vc.put("date", formatter.format(new Date()));

        for (ClassDoc classDoc : root.classes()) {
            classDocs.put(classDoc.qualifiedName(), classDoc);
        }

        final List<Resource> resources = new ArrayList<>();

        // pre-filter @Controllers
        AnnotationTypeDoc type;
        for (ClassDoc classDoc : root.classes()) {
            Resource resource = new Resource();
            String paths[] = { "" };
            for (AnnotationDesc classAnnotation : classDoc.annotations()) {
                type = classAnnotation.annotationType();

                if ("org.springframework.stereotype.Controller".equals(type.qualifiedName())) {
                    resource.setClassDoc(classDoc);
                    // LOG.info("========= @Controller " +
                    // classDoc.qualifiedName() + " ========");
                } else if ("org.springframework.web.bind.annotation.RestController".equals(type.qualifiedName())) {
                    resource.setClassDoc(classDoc);
                } else if ("org.springframework.web.bind.annotation.ControllerAdvice".equals(type.qualifiedName())) {
                    resource.setClassDoc(classDoc);
                    // LOG.info("========= @ControllerAdvice " +
                    // classDoc.qualifiedName() + " ======== type" + type
                    // .qualifiedName());
                } else if ("org.springframework.web.bind.annotation.RequestMapping".equals(type.qualifiedName())) {
                    paths = getValue(classAnnotation, "value");
                } else  if (AuthorizationTest.class.getName().equals(type.qualifiedName()) || 
                        "com.sma.security.annotation.Authorization".equals(type.qualifiedName())) {
//                    //AuthorizationTest is for docrest testing , 
//                    // and Authorization it is real annotation to be used in project integrated with user-security layer                   
//                       //get internal define ROLEs ROLE_USER
                        String[] userRoles =getValue(classAnnotation, "userRoles");
                        StringBuilder traits = new StringBuilder();
                        String comma = "";
//                        //add all roles
                        for (String userRole : userRoles) {
                            traits.append(comma).append(userRole);
                            comma = ",";
                        }
                        resource.addTrait(traits.toString());
//                        method.addTrait(traits.toString());
//                    }
                    
                } else if (RestReturn.class.getName().equals(type.qualifiedName())) {
                    resource.setIncludeApi(true);
                    resource.setName(classDoc.qualifiedName());

                    // classAnnotation.elementValues() return only the
                    // explicitly listed elements;
                    // the default ones will not be returned;
                    // for example, RestReturn.value will not be returned by the
                    // method if it's not explicitly
                    // specified even though it has the default value
                    for (ElementValuePair element : classAnnotation.elementValues()) {
                        // set as default resource value of entity, and method
                        // entity, if not specified
                        if ("value".equals(element.element().name())) {
                            final String className = element.value().value().toString();
                            resource.setEntityType(className);
                            resource.setSimpleType(className);
                            int beginIndex = className.lastIndexOf('.');
                            if (-1 < beginIndex) {
                                resource.setSimpleType(className.substring(beginIndex + 1));
                            }
                        }
//                            else if ("isSecured".equals(element.element().name())
//                                && Boolean.TRUE.equals(getValueAsObject(classAnnotation, "isSecured"))) {
//                            resource.addTrait("Secured");
//
//                        }
                    }

                    // If RestReturn.value or RestReturn.entity are not
                    // explicitly specified,
                    // set the return type and entity type to the default values
                    // of RestReturn.value and RestReturn
                    // .entity respectively
                    if (resource.getEntityType() == null || resource.getSimpleType() == null) {
                        for (AnnotationTypeElementDoc element : type.elements()) {
                            if ("value".equals(element.name())) {
                                final String className = element.defaultValue().toString();
                                resource.setEntityType(className);
                                resource.setSimpleType(className);
                                int beginIndex = className.lastIndexOf('.');
                                if (-1 < beginIndex) {
                                    resource.setSimpleType(className.substring(beginIndex + 1));
                                }
                            }
                        }
                    }

                    // LOG.info("========= @RestReturn " +
                    // classDoc.qualifiedName() + " ========");
                    // LOG.info(String.format("             entity = %s",
                    // resource.getEntityType()));
                }
            }

            // in case of @Controller and its RestReturn.entity (default value =
            // RestReturn.value)

            if (null != resource.getClassDoc()) {

                resource.setPaths(paths);
                // traverse ancestors
                LOG.info(">>> " + classDoc.qualifiedName());
                boolean include = traverseAncestors(classDoc, resource);
                // if (include && null != resource.getEntityType()) {
                if (include && resource.isIncludeApi()) {
                    resources.add(resource);
                }
            }

        }

        vc.put("helper", this);
        vc.put("resources", resources);
        vc.put("encoder", new StringEscapeUtils());
        vc.put("jsonClass", jsonClassMap);
        vc.put("jsonDoc", jsonDocMap);
        
        vc.put("readme", getReadme());

        String path;
        Collection<Method> methods;
        for (Resource r : resources) {
            for (String rp : r.getPaths()) {
                for (Method m : r.getMethods()) {
                    for (String mp : m.getPaths()) {
                        path = String.format("%s/%s", rp, mp);
                        methods = r.getOperationsMap().get(path);
                        if (null == methods) {
                            methods = new TreeSet<Method>();
                            r.getOperationsMap().put(path, methods);

                        }
                        methods.add(m);

                    }
                }
            }
        }
        //sort resources
        Collections.sort(resources, new Comparator<Resource>() {
            @Override
            public int compare(Resource o1, Resource o2) {
                // return
                // o1.getEntityType().compareToIgnoreCase(o2.getEntityType());
             // return o1.getName().compareToIgnoreCase(o2.getName()); // compare with controller name
                return o1.getPaths()[0].compareToIgnoreCase(o2.getPaths()[0]); // compare path 
                
            }
        });
        
        return resources;
    }

    /**
     * Reading the class methods and its parents' methods as well.
     */
    protected boolean traverseAncestors(ClassDoc classDoc, Resource resource) {

        // Collect information about the class hierarchy
        // Can be useful when dealing with framework code and generic classes
        List<String> classHierarchy = resource.getClassHierarchy();
        if (null == classHierarchy) {
            classHierarchy = new ArrayList<String>();
            resource.setClassHierarchy(classHierarchy);
        }
        classHierarchy.add(classDoc.qualifiedTypeName());

        boolean include = traverseMethods(classDoc, resource);

        // recursion with it parents
        // now do the same for parent:
        if (!Object.class.getName().equals(classDoc.superclassType().qualifiedTypeName())) {
            include |= traverseAncestors(classDoc.superclass(), resource);
        }

        return include;
    }

    protected boolean traverseMethods(ClassDoc classDoc, Resource resource) {
        AnnotationTypeDoc type;
        boolean include = false;
        // find mapped methods
        for (MethodDoc methodDoc : classDoc.methods()) {
            boolean includeMethod = false;
            Method method = new Method(resource);
            StringBuilder traits = new StringBuilder();
            String comma = "";
            //Authorization annotation    
            for (AnnotationDesc methodAnnotation : methodDoc.annotations()) {
                type = methodAnnotation.annotationType();
                //AuthorizationTest is for docrest testing , 
                // and Authorization it is real annotation to be used in project integrated with user-security layer
                if (AuthorizationTest.class.getName().equals(type.qualifiedName()) || 
                        "com.sma.security.annotation.Authorization".equals(type.qualifiedName())) {
                   //get internal define ROLEs ROLE_USER
                    String[] userRoles =getValue(methodAnnotation, "userRoles");
                    
                    //add all roles
                    for (String userRole : userRoles) {
                        
                        traits.append(comma).append(userRole);
                        comma = ",";
                    }
                    LOG.info(traits.toString());
                    method.addTrait(traits.toString());
                }
                if ("org.springframework.web.bind.annotation.RequestMapping".equals(type.qualifiedName())) {
                    // LOG.info("---- @RequestMapping " +
                    // classDoc.simpleTypeName() + "." + methodDoc.name() + "()
                    // ----");
                    method.setClassDoc(classDoc);
                    method.setMethodDoc(methodDoc);
                    method.setName(methodDoc.name());
                    method.setPaths(getValue(methodAnnotation, "value"));
                    String m[] = getValue(methodAnnotation, "method");
                    StringBuilder methodType = new StringBuilder();
                    //In case there is no request method found, m[] size is 1 with an empty value
                    if (m.length > 0 && m.length < 7 && m[0] != null && !m[0].isEmpty()) {

                        for (String requestMethod : m) {
                            if (requestMethod.startsWith("org.springframework.web.bind.annotation.RequestMethod.")) {
                                String methodName = requestMethod
                                        .substring("org.springframework.web.bind.annotation.RequestMethod.".length()) ;
                                methodType.append(methodName).append(", ");
                                //consume for content-type with POST and PUT
                                if (Arrays.asList("POST","PUT").contains(methodName)) {
                                    final String[] consumes = getValue(methodAnnotation, "consumes");
                                    for (String consume : consumes) {
                                        LOG.info("========consumes-name ======== " + consume);
                                        if (StringUtils.isEmpty(consume)) {
                                            method.setContentType("application/x-www-form-urlencoded charset=utf-8");
                                        } else if (StringUtils.isEmpty(method.getContentType())) {
                                            method.setContentType(consume);
                                        } else {
                                            method.setContentType(String.format("%s, %s", method.getContentType(), consume));
                                        }
                                    }
                                }
                            }
                        }

                        if (methodType.length() == 0) {
                            // Request mapping does not have a request type
                            // (GET, POST etc)
                            String message = "Fatal error: @RequestMapping " + classDoc.simpleTypeName() + "." + methodDoc.name()
                                    + " is missing request type";
                            LOG.info(message);
                            throw new IllegalArgumentException(message);
                        }

                        method.setMethod(methodType.toString().substring(0, methodType.length() - 2));
                    } else {
                        method.setMethod("*");
                    }
                    
                    //consumes = { MediaType.APPLICATION_JSON_VALUE } , ONLY
                    //method.setContentType(getValue(methodAnnotation, "consumes").toString());
                    
                    traverseParameters(methodDoc, method);

                }

                if (RestReturn.class.getName().equals(type.qualifiedName())) {
                    method.setSupportsClassParams(Boolean.TRUE.equals(getValueAsObject(methodAnnotation, "supportsClassParams")));
                    // LOG.info("       supportsClassParams=" +
                    // method.isSupportsClassParams());

                    // Set secured to the same values as the rest resource as
                    // default
                    // Can be overwritten by annotation on method level
                   // if (resource.hasTrait("Secured")) {
                   //     method.addTrait("Secured");
                   // }

                    //
                    include = true;
                    includeMethod = true;

                    // LOG.info("---- @RestReturn " + methodDoc.name() +
                    // "() of " + classDoc.simpleTypeName() + "
                    // ----");
                    method.setRestReturn(methodAnnotation);

                    method.setHighlightApiMessage("");
                    // methodAnnotation.elementValues() return only the
                    // explicitly listed elements;
                    // the default ones will not be returned;
                    // for example, RestReturn.value will not be returned by the
                    // method if it's not explicitly
                    // specified even though it has the default value
                    for (ElementValuePair element : methodAnnotation.elementValues()) {
                        if ("value".equals(element.element().name())) {
                            method.setReturnType(element.value().value().toString());
                            // if (){
                            // T ?
                            // methodDoc.getReturType().getGenericType()
                            // }
                            // LOG.info("---------------" +
                            // element.value().value().toString());
                        } else if ("entity".equals(element.element().name())) {
                            method.setEntityType(element.value().value().toString());
//                        } else if ("isSecured".equals(element.element().name())) {
//                            if (Boolean.TRUE.equals(getValueAsObject(methodAnnotation, "isSecured"))) {
//                                method.addTrait("Secured");
//                            } else {
//                                method.removeTrait("Secured");
//                            }
                        } else if ("highlightApiMessage".equals(element.element().name())) {
                            //LOG.info("===============highlight messages================");
                            if (!"".equals((String)getValueAsObject(methodAnnotation, "highlightApiMessage"))) {
                                method.setHighlightApiMessage((String)getValueAsObject(methodAnnotation, "highlightApiMessage"));
                                LOG.info(">>>>>>>>>>>>>>>>>>>>>>>> " + method.getHighlightApiMessage());
                            }
                        }
                    }

                    // If RestReturn.value or RestReturn.entity are not
                    // explicitly specified,
                    // set the return type and entity type to the default values
                    // of RestReturn.value and RestReturn
                    // .entity respectively
                    if (method.getReturnType() == null || method.getEntityType() == null) {
                        for (AnnotationTypeElementDoc element : type.elements()) {
                            if ("value".equals(element.name())) {
                                method.setReturnType(element.defaultValue().toString());
                            } else if ("entity".equals(element.name())) {
                                method.setEntityType(element.defaultValue().toString());
                            }
                        }
                    }
                }
            }

            if (includeMethod) {
                resource.getMethods().add(method);
            }

            if (null != method.getReturnType()) {

                // value include entity as json member
                // list json object for @RestReturn.value
                method.setJson(getJson(method.getReturnType(), method.getEntityType()));
                // list json object for @RestReturn.entity
                // method.setJson(getJson(method.getReturnType(),
                // method.getReturnType()));
                method.setJsonEntity(getJson(method.getReturnType(), null));
            }
            
            //override Authorization Role from Resources
            if (CollectionUtils.isEmpty(method.getTraits()) && CollectionUtils.isNotEmpty(resource.getTraits())) {
                method.setTraits(resource.getTraits());
            }
        }
        return include;
    }

    protected void traverseParameters(MethodDoc methodDoc, Method method) {
        AnnotationTypeDoc type;

        for (Parameter p : methodDoc.parameters()) {
            LOG.info(method.getName() + " :       parameter " + p.typeName() + " " + p.name());
            for (AnnotationDesc paramAnnotation : p.annotations()) {
                type = paramAnnotation.annotationType();

                Param param = new Param();
                param.setName(p.name());
                param.setComment(getComment(methodDoc, p.name()));

                // set as default
                param.setRequired(true);
                param.setDefaultValue("");

                if ("org.springframework.web.bind.annotation.PathVariable".equals(type.qualifiedName())) {

                    param.setType(p.typeName());
                    param.setName(String.format("{%s}", p.name()));
                    for (ElementValuePair elementValue : paramAnnotation.elementValues()) {
                        if ("value".equals(elementValue.element().name())) {
                            param.setName(String.format("{%s}", elementValue.value().value().toString()));
                        }
                    }
                    method.getPathVariables().add(param);

                } else if ("org.springframework.web.bind.annotation.RequestBody".equals(type.qualifiedName())) {
                    // param.setType(p.typeName());
                    // param.setJson(getJson(p.type().toString(), null));
                  //supportsClassParams true and param must genericEntity
                    if ((method.isSupportsClassParams() && "genericEntity".equals(param.getName()))
                            || "java.lang.Object".equals(p.type().qualifiedTypeName())) {
                        try {
                        param.setType(getJson(method.getReturnType(), method.getEntityType()));
                        param.setType2(method.getReturnType());
                        } catch (Exception e) {
                            LOG.warning(method.getMethod() + " error : " + p.name() +  e.getMessage());
                            param.setType(getJson(p.type().qualifiedTypeName(), method.getEntityType()));
                            param.setType2(p.type().qualifiedTypeName());
                        }
                    } else {
                        LOG.info(method.getMethod() + " : " + p.name() +  p.type().qualifiedTypeName());
                        
                        param.setType(getJson(p.type().qualifiedTypeName(), null));
                        param.setType2(p.type().qualifiedTypeName());
                    }

                    for (ElementValuePair elementValue : paramAnnotation.elementValues()) {

                        if ("required".equals(elementValue.element().name())) {
                            param.setRequired(Boolean.valueOf(elementValue.value().toString()));
                        }
                    }
                    // LOG.info(">>>>>>>>>>>>>>>>>> 1" + param.isRequired());
                    method.setBody(param);
                    // method.getParameters().add(param);
                } else if ("org.springframework.web.bind.annotation.RequestParam".equals(type.qualifiedName())) {
                    param.setType(p.typeName());
                    // update
                    for (ElementValuePair elementValue : paramAnnotation.elementValues()) {
                        if ("value".equals(elementValue.element().name())) {
                            param.setName(elementValue.value().value().toString());
                        }

                        if ("org.springframework.web.bind.annotation.RequestParam.required".equals(elementValue.element()
                                .qualifiedName())) {
                            param.setRequired(Boolean.valueOf(elementValue.value().toString()));
                        } else if ("org.springframework.web.bind.annotation.RequestParam.defaultValue".equals(elementValue
                                .element().qualifiedName())) {
                            param.setDefaultValue(elementValue.value().toString());
                            param.setRequired(false);
                        }
                    }

                    method.getParameters().add(param);
                } else if ("org.springframework.web.bind.annotation.ModelAttribute".equals(type.qualifiedName())) {

                    if ("java.lang.Object".equals(p.type().qualifiedTypeName())) {
                        LOG.info("generic class Param : get class : get @RestReturn of Class Level: " + method.getEntityType() );
                        try {
                            param.setType(getJson(method.getReturnType(), method.getEntityType()));
                        } catch (Exception e) {
                            LOG.warning(method.getMethod() + " error : " + p.name() +  e.getMessage());
                            param.setType(getJson(p.type().qualifiedTypeName(), null));
                        }
                    } else {
                        //supportsClassParams true and param must genericEntity
                        if (method.isSupportsClassParams() && "genericEntity".equals(param.getName())) {
                            param.setType(getJson(method.getReturnType(), method.getEntityType()));
                        } else {
                            param.setType(getJson(p.type().qualifiedTypeName(), null));
                        }
                    }
                    method.getModelAttributes().add(param);
                }
            }
        }

    }
}
