package gov.cms.qpp.generator;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import gov.cms.qpp.conversion.model.error.ErrorCode;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Mojo(name = "generateErrorCodeDoc")
public class ErrorCodeDocumentationGenerator extends AbstractMojo {

	public static void main(String... args) throws IOException {
		MustacheFactory mf = new DefaultMustacheFactory();
		Mustache mdTemplate = mf.compile("error-code/error-code-tempate.md");

		try (FileWriter fw = new FileWriter(args[0] + "ERROR_MESSAGES.md")) {
			List<ErrorCode> errorCodes = Arrays.asList(ErrorCode.values());
			mdTemplate.execute(fw, errorCodes).flush();
			fw.flush();
		}
	}

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		String parentDir = "";
		
		try {
			getLog().info("Running Error Code documentation plugin");
			
			@SuppressWarnings("rawtypes")
			Map context = getPluginContext();
			MavenProject project = (MavenProject) context.get("project");
			MavenProject parent = project.getParent();
			
			if (parent != null) {
				String parentPath = parent.getBasedir().getAbsolutePath();
				String workingDir = new File(".").getAbsolutePath();
				workingDir = workingDir.substring(0, workingDir.length() - 2);
				
				if (parentPath.equals(workingDir)) {
					// when the working dir is the parent project dir, use the working dir
					parentDir = "./";
				} else {
					// when the working dir is the subproject dir, use the parent dir
					// this ensure the error messages file is written to the parent project dir
					parentDir = "../"; 
				}
			}
			getLog().info("Parent project work directory offset " + parentDir);
			
			ErrorCodeDocumentationGenerator.main(parentDir.toString());
		} catch (IOException e) {
			throw new MojoExecutionException("Error code documentation problems", e);
		}
	}
}
