#http://proguard.sourceforge.net/manual/examples.html
#http://proguard.sourceforge.net/manual/usage.html
-keepparameternames
-keepclasseswithmembers class * {
    native <methods>;
}
-keepclasseswithmembers class * {
    public <init>(android.content.Context);
}
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keepclasseswithmembers class com.danielworld.graph.util.ChartDateUtil {
	public static <methods>;
}

-keepclasseswithmembers enum com.danielworld.graph.util.ChartDays {
	*;
}
-keepclasseswithmembers class com.danielworld.graph.chart.Chart {
    public *;
}
-keep class com.danielworld.graph.model.** {
    *;
}
-keepclasseswithmembers interface com.danielworld.graph.ChartData {
    *;
}
-keep class com.danielworld.graph.chart.LineChart {
    *;
}

