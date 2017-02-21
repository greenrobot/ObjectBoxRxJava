RXJava 2 APIs for ObjectBox
===========================
While ObjectBox brings some reactive features out of the box, this project brings RxJava 2 support.  

Use the class RxQuery to interact with Query objects using:
 * Flowable
 * Observable
 * Single

For general Object changes, you can use RxBoxStore to create an Observable.

Sample Code
-----------
Get query results and subscribe to future updates (Object changes will automatically emmit new data):

```java
Query query = box.query().build();
RxQuery.observable(query).subscribe(this);
```
    
Gradle dependency
-----------------
Ensure you are using the latest Version (this page may become out of date).

Gradle:
```gradle
compile 'io.objectbox:objectbox-rxjava:0.9.8'
```

License
-------
Copyright (C) 2017, greenrobot/ObjectBox (http://greenrobot.org)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.