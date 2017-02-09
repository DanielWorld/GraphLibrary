#http://proguard.sourceforge.net/manual/examples.html
#http://proguard.sourceforge.net/manual/usage.html
-keepparameternames
-keepclasseswithmembers class * {
    native <methods>;
}

-keepclasseswithmembers class com.danielworld.graph.util.ChartDateUtil {
	public static <methods>;
}

-keepclasseswithmembers enum com.danielworld.graph.util.ChartDays {
	*;
}

