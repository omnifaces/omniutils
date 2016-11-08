module org.omnifaces.omniutils {
	exports org.omnifaces.utils;
	exports org.omnifaces.utils.annotation;
	exports org.omnifaces.utils.collection;
	exports org.omnifaces.utils.exceptions;
	exports org.omnifaces.utils.function;
	exports org.omnifaces.utils.io;
	exports org.omnifaces.utils.logging;
	exports org.omnifaces.utils.math;
	exports org.omnifaces.utils.properties;
	exports org.omnifaces.utils.security;
	exports org.omnifaces.utils.stream;
	exports org.omnifaces.utils.text;
	exports org.omnifaces.utils.time;

	requires transitive java.logging;

	// Optional module dependencies
	requires static java.desktop;
}