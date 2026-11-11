CÓMO USARLO
1. Android Studio > New Project > "Empty Views Activity" (Java), package: com.comunidad.app, minSdk 24.
2. Borra MainActivity.java, activity_main.xml y el themes.xml generados.
3. Copia el contenido de esta carpeta:  java/ -> app/src/main/java/ | res/ -> app/src/main/res/ | AndroidManifest.xml reemplaza el existente.
4. En app/build.gradle (dependencies) asegúrate de tener:
     implementation 'com.google.android.material:material:1.11.0'
     implementation 'androidx.appcompat:appcompat:1.6.1'
     implementation 'androidx.recyclerview:recyclerview:1.3.2'
     implementation 'androidx.fragment:fragment:1.6.2'
5. Sync y Run.
Nota: el primer usuario que se registra en una comunidad queda como administrador.
