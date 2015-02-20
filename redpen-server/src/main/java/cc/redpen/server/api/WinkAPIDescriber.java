package cc.redpen.server.api;

import org.apache.wink.common.annotations.Workspace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import java.lang.annotation.*;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Simple WINK REST API describer
 */
@Workspace(workspaceTitle = "API", collectionTitle = "API Documentation")
@Path("/")
public class WinkAPIDescriber {
    protected static final Logger LOG = LoggerFactory.getLogger(WinkAPIDescriber.class);

    protected static final Class[] REST_API_CLASSES = {
            RedPenResource.class,
            RedPenConfigurationResource.class
    };


    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD, ElementType.METHOD, ElementType.TYPE})
    public @interface Description {
        String value();
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    @Description("RedPen API Description")
    public String describeRestAPI(@Context UriInfo uriInfo) {
        StringBuilder sb = new StringBuilder();

        sb.append("<html>\n");
        sb.append("<head>\n");
        sb.append("<title>REST API Description</title>\n");
        sb.append("<style>\n");
        sb.append("table {font:0.75em Verdana, Arial, Helvetica, sans-serif; line-height:0.75em; border-collapse: collapse; }");
        sb.append("th {padding: 0.5em; padding-top: 2em; text-align: left; border:none;}");
        sb.append("tr, td {border-bottom: 1px dotted #fafafa;}");
        sb.append("td {vertical-align:top;border:none; padding: 0.5em 0.5em; background-color: #fefefe }");
        sb.append("</style>\n");
        sb.append("</head>\n");
        sb.append("<body>\n");
        sb.append("<table class=\"api_table\">\n");

        List<Class<?>> sortedClasses = new ArrayList<Class<?>>(Arrays.<Class<?>>asList(REST_API_CLASSES));

        Collections.sort(sortedClasses, new Comparator<Class<?>>() {
            @Override
            public int compare(Class<?> o1, Class<?> o2) {
                return o1.getSimpleName().compareTo(o2.getSimpleName());
            }
        });
        for (Class<?> clazz : sortedClasses) {
            describe(clazz, sb, uriInfo.getAbsolutePath().getPath());
        }

        sb.append("</table>\n");
        sb.append("<body>\n");
        sb.append("</html>\n");

        return sb.toString();
    }

    private void describe(Class<?> clazz, StringBuilder sb, String baseUrl) {
        if (clazz != WinkAPIDescriber.class) {

            if (baseUrl.endsWith("/")) {
                baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
            }

            Path principalPath = clazz.getAnnotation(Path.class);
            Workspace workspace = clazz.getAnnotation(Workspace.class);
            if (principalPath != null) {
                String mainPath = principalPath.value();
                String description = workspace == null ? "@Workspace annotation is missing from " + clazz.getSimpleName() : workspace.workspaceTitle() + ": " + workspace.collectionTitle();

                sb.append("<tr>");
                sb.append("<th colspan=\"3\" class=\"api_title\">");
                sb.append(description);
                sb.append("</th>");
                sb.append("</tr>\n");

                Method[] methods = clazz.getMethods();
                Arrays.<Method>sort(methods, new Comparator<Method>() {
                    @Override
                    public int compare(Method o1, Method o2) {
                        Path path = o1.getAnnotation(Path.class);
                        Path path2 = o2.getAnnotation(Path.class);

                        String v1 = (path == null) ? "" : path.value();
                        String v2 = (path2 == null) ? "" : path2.value();

                        return v1.compareTo(v2);
                    }
                });
                for (Method method : methods) {
                    Path path = method.getAnnotation(Path.class);
                    if (path != null) {
                        sb.append("<tr>");
                        sb.append("<td class=\"api_link\">");
                        String url = mainPath + path.value();
                        sb.append("<a href=\"" + baseUrl + url + "\"/>" + url + "</a>");
                        sb.append("</td>");
                        sb.append("<td class=\"api_parameters\">");
                        for (Annotation[] annotations : method.getParameterAnnotations()) {
                            for (Annotation annotation : annotations) {
                                if (annotation instanceof QueryParam) {
                                    QueryParam param = (QueryParam) annotation;
                                    sb.append(param.value());
                                    for (Annotation annotations2 : annotations) {
                                        if (annotations2 instanceof DefaultValue) {
                                            String value = ((DefaultValue) annotations2).value();
                                            if ((value != null) && !value.isEmpty()) {
                                                sb.append("<i>=" + value + "</i>");
                                            }
                                        }
                                    }
                                } else if (annotation instanceof FormParam) {
                                    FormParam param = (FormParam) annotation;
                                    sb.append(param.value());
                                    sb.append("(POST)");
                                    for (Annotation annotations2 : annotations) {
                                        if (annotations2 instanceof DefaultValue) {
                                            String value = ((DefaultValue) annotations2).value();
                                            if ((value != null) && !value.isEmpty()) {
                                                sb.append("<i>=" + value + "</i>");
                                            }
                                        }
                                    }
                                } else if (annotation instanceof Context) {
                                    sb.append("name=value...");
                                }
                                sb.append("<br/>");
                            }
                        }
                        sb.append("</td>");

                        Description methodDescriptionAnnotation = method.getAnnotation(Description.class);
                        String methodDescription = "";
                        if (methodDescriptionAnnotation != null) {
                            methodDescription = methodDescriptionAnnotation.value();
                        }
                        sb.append("<td class=\"api_description\">");
                        sb.append(methodDescription);
                        sb.append("</td>");
                        sb.append("</tr>\n");
                    }
                }
            }
        }
    }
}