module.exports = function(ctx) {

		var fs = ctx.requireCordovaModule('fs'),
			path = ctx.requireCordovaModule('path'),
			deferral = ctx.requireCordovaModule('q').defer();

		var gradleFile = path.join(ctx.opts.projectRoot, 'platforms/android/build.gradle');
		
    var replaceStr =    `    // and https://issues.apache.org/jira/browse/CB-8143
    dependencies {`;
    	var newStr = `    // and https://issues.apache.org/jira/browse/CB-8143
    dependencies {
       classpath "com.mob.sdk:MobSDK:+"`;

		fs.readFile(gradleFile, function(err, data) {
			if (err) deferral.reject('readFile failed');

		 if(data.toString().indexOf(newStr) <0){
		 	 deferral.resolve();
		   return deferral.promise;
		}
     	var result = data.toString().replace(newStr, replaceStr);
			fs.writeFile(gradleFile, result, 'utf8', function (err) {
                    if (err) deferral.reject('writeFile failed');
                    console.log("Remove MobSDK dependencies from build.gradle success Done!");
                    deferral.resolve();
           });

			deferral.resolve();

			});

			return deferral.promise;
}