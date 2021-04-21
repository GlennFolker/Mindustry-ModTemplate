package template.automation;

import arc.*;
import arc.assets.*;
import arc.assets.loaders.SoundLoader.*;
import arc.audio.*;
import arc.files.*;
import arc.util.*;
import mindustry.*;

import java.util.*;

import javax.annotation.processing.*;
import javax.lang.model.*;
import javax.lang.model.element.*;
import javax.tools.*;

import com.squareup.javapoet.*;

@SupportedAnnotationTypes("java.lang.Override")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class AssetsPlugin extends AbstractProcessor {
    private Fi assetsDir;
    private Filer filer;
    private boolean done = false;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        filer = processingEnv.getFiler();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if(done) return false;
        try {
            if(assetsDir == null) {
                String path = Fi.get(filer.getResource(StandardLocation.CLASS_OUTPUT, "no", "no")
                    .toUri().toURL().toString().substring(OS.isWindows ? 6 : "file:".length()))
                    .parent().parent().parent().parent().parent().parent().toString().replace("%20", " ");

                assetsDir = Fi.get(path).child("assets/");
            }

            processSounds();
            done = true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return false;
    }

    private ClassName cName(Class<?> type) {
        return ClassName.get(type);
    }

    private String lnew() {
        return Character.toString('\n');
    }

    private void processSounds() throws Exception{
        ClassName ctype = cName(Sound.class);
        TypeSpec.Builder soundSpec = TypeSpec.classBuilder("ModSounds").addModifiers(Modifier.PUBLIC)
            .addJavadoc("Mod's {@link $T} effects.", ctype)
            .addMethod(
                MethodSpec.methodBuilder("loadSound").addModifiers(Modifier.PROTECTED, Modifier.STATIC)
                    .addJavadoc(
                        CodeBlock.builder()
                            .add("Loads a {@link $T}." + lnew(), ctype)
                            .add("@param soundName The {@link $T} name." + lnew(), ctype)
                            .add("@return The {@link $T}.", ctype)
                        .build()
                    )
                    .returns(ctype)
                    .addParameter(cName(String.class), "soundName")
                    .beginControlFlow("if(!$T.headless)", cName(Vars.class))
                        .addStatement("$T name = $S + soundName", cName(String.class), "sounds/")
                        .addStatement("$T path = $T.tree.get(name + $S).exists() ? name + $S : name + $S", cName(String.class), cName(Vars.class), ".ogg", ".ogg", ".mp3")
                        .addCode(lnew())
                        .addStatement("var sound = new $T()", ctype)
                        .addCode(lnew())
                        .addStatement("$T<?> desc = $T.assets.load(path, $T.class, new $T(sound))", cName(AssetDescriptor.class), cName(Core.class), ctype, cName(SoundParameter.class))
                        .addStatement("desc.errored = $T::printStackTrace", cName(Throwable.class))
                        .addCode(lnew())
                        .addStatement("return sound")
                    .nextControlFlow("else")
                        .addStatement("return new $T()", ctype)
                    .endControlFlow()
                .build()
            )
            .addMethod(
                MethodSpec.methodBuilder("disposeSound").addModifiers(Modifier.PROTECTED, Modifier.STATIC)
                    .addJavadoc(
                        CodeBlock.builder()
                            .add("Disposes a {@link $T}." + lnew(), ctype)
                            .add("@param soundName The {@link $T} name." + lnew(), ctype)
                            .add("@return {@code null}.")
                        .build()
                    )
                    .returns(ctype)
                    .addParameter(cName(String.class), "soundName")
                    .beginControlFlow("if(!$T.headless)", cName(Vars.class))
                        .addStatement("$T name = $S + soundName", cName(String.class), "sounds/")
                        .addStatement("$T path = $T.tree.get(name + $S).exists() ? name + $S : name + $S", cName(String.class), cName(Vars.class), ".ogg", ".ogg", ".mp3")
                        .addCode(lnew())
                        .beginControlFlow("if($T.assets.isLoaded(path, $T.class))", cName(Core.class), ctype)
                            .addStatement("$T.assets.unload(path)", cName(Core.class))
                        .endControlFlow()
                    .endControlFlow()
                    .addCode(lnew())
                    .addStatement("return null")
                .build()
            );
        MethodSpec.Builder load = MethodSpec.methodBuilder("load").addModifiers(Modifier.PUBLIC, Modifier.STATIC)
            .addJavadoc("Loads all {@link $T}s.", ctype)
            .returns(TypeName.VOID);

        MethodSpec.Builder dispose = MethodSpec.methodBuilder("dispose").addModifiers(Modifier.PUBLIC, Modifier.STATIC)
            .addJavadoc("Disposes all {@link $T}s.", ctype)
            .returns(TypeName.VOID);

        String dir = "main/assets/sounds/";
        assetsDir.child("sounds").walk(path -> {
            String p = path.absolutePath();
            String name = p.substring(p.lastIndexOf(dir) + dir.length(), p.length());
            String fname = path.nameWithoutExtension();
            int ex = 4;

            soundSpec.addField(
                FieldSpec.builder(
                    ctype,
                    Strings.kebabToCamel(fname),
                    Modifier.PUBLIC, Modifier.STATIC
                )
                .build()
            );

            String stripped = name.substring(0, name.length() - ex);
            load.addStatement("$L = loadSound($S)", Strings.kebabToCamel(fname), stripped);
            dispose.addStatement("$L = disposeSound($S)", Strings.kebabToCamel(fname), stripped);
        });

        soundSpec
            .addMethod(load.build())
            .addMethod(dispose.build());

        JavaFile.builder("template.gen", soundSpec.build())
            .indent("    ")
            .skipJavaLangImports(true)
        .build().writeTo(filer);
    }

    @Override
    public SourceVersion getSupportedSourceVersion(){
        return SourceVersion.RELEASE_8;
    }
}
