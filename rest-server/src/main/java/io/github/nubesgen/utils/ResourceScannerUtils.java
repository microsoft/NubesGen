package io.github.nubesgen.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Predicate;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.support.ResourcePatternResolver;

/**
 * Utility class that scans for resources/directories in GraalVM native image
 * Workaround for https://github.com/oracle/graal/issues/1108
 * This is a sample code how to use it:
 * * * Predicate<Resource> directoryPredicate = resource ->
 * !Objects.requireNonNull(resource.getFilename()).contains(".") ||
 * ".github".equals(resource.getFilename());
 * * * Resource[] resources = ResourceScannerUtils.getResourceFiles(new
 * PathMatchingResourcePatternResolver(), "classpath*:myDirectoryToScan",
 * directoryPredicate);
 * 
 * @author bnasslahsen
 */
public final class ResourceScannerUtils {

	/**
	 * Instantiates a new Resource scanner.
	 */
	private ResourceScannerUtils() {
	}

	/**
	 * Get resource files resource within the directory[ ].
	 *
	 * @param resolver           the Spring Resource Pattern Resolver
	 * @param locationDir        the directory to scan (recursively)
	 * @param directoryPredicate the condition to determine it's a directory from
	 *                           the available resources in the classpath
	 * @return the resource [ ] An array, of the available resources within the
	 *         directory
	 * @throws IOException the io exception
	 */
	public static Resource[] getResourceFiles(ResourcePatternResolver resolver, String locationDir,
			Predicate<Resource> directoryPredicate) throws IOException {
		Resource[] resourcesArray = resolver.getResources(locationDir);
		Set<Resource> resourceSet = new LinkedHashSet<>();
		for (Resource resource : resourcesArray)
			resourceSet.addAll(getResourceFilesRecursively(resource, resourceSet, directoryPredicate));
		return resourceSet.toArray(new Resource[0]);
	}

	/**
	 * Gets resource files recursively.
	 *
	 * @param urlResource        the url resource
	 * @param resourceSet        the resource set
	 * @param directoryPredicate the directory predicate
	 * @return the resource files recursively
	 * @throws IOException the io exception
	 */
	private static Set<Resource> getResourceFilesRecursively(Resource urlResource, Set<Resource> resourceSet,
			Predicate<Resource> directoryPredicate) throws IOException {
		try (BufferedReader br = new BufferedReader(new InputStreamReader(urlResource.getInputStream()))) {
			String resource;
			while ((resource = br.readLine()) != null) {
				Resource resourceIn = new UrlResource(urlResource.getURL() + "/" + resource);
				resourceSet.add(resourceIn);
				if (directoryPredicate.test(resourceIn))
					getResourceFilesRecursively(resourceIn, resourceSet, directoryPredicate);
			}
		}
		return resourceSet;
	}
}
