# Mindustry-ModTemplate
A multi-module [**Mindustry**](https://github.com/Anuken/Mindustry) mod template that allows you to to have Java 9+ syntaxes, automation of some assets loading, and other various exclusive modules.

## Requirements
- [**Git**](https://git-scm.com/).
- [**Java**](https://adoptopenjdk.net/releases.html) 8 or higher.
- A **text editor**, preferably a Java IDE. [**IntelliJ IDEA**](https://www.jetbrains.com/idea/download/), [**Eclipse**](https://www.eclipse.org/downloads/), or [**Visual Studio Code**](https://code.visualstudio.com/download) works fine.

## Usage
1. Learning Java itself is mandatory for your own good. If you don't have a moderate Java knowledge, learn them first before stepping into making Mindustry mods.
2. Generate a new repository with this template.
3. Clone the repository to your end:
    - Open your command line, then clone your repository by executing `git clone https://github.com/<YourUsername>/<YourRepository>`. `cd` to the directory of the cloned repository to set the current working directory.
4. Change **every** package declaration and folder names into what you desire. For example, the current packages are `template.**` and you want to change to `modname.**`:
    - Replace the folder directory from all modules from `src/template/**` to `src/modname/**`.
    - Go through every Java file and replace `package template.**` with `package modname.**`, and all `import template.**` with `import modname.**`.
    - If you found anything that is related to package declaration, change it to match your mod's own package name.
5. Go through every file and replace everything necessary *(usually noted with `[subject]` in the comment)*.
6. Track the edited file by executing `git add .`. Commit your changes by executing `git commit -m <message>`, then push your changes to GitHub by executing `git push`.
7. 
    1. At the first time cloning and refactoring, your IDE might scream **"The import `modname.gen` cannot be resolved"**. It is because you haven't built the project and therefore the generated classes won't be generated. Compiling the project should fix it.
    2. Note that annotation processors are ran before the main codes compile. Don't be afraid to refer to the currently missing generated classes before you compile; such as referring sound fields after you add a new sound.

## Compiling
Install Java 8 or higher *(as referenced in [**#Requirements**](#Requirements))*.

### Windows
`gradlew main:dist`: Creates a distributable `.jar` compatible only on desktop version. <br>
`gradlew main:dex`: Dexifies the desktop `.jar` file to be used [**here**](#L26). <br>
`gradlew main:distDex`: Combines the desktop `.jar` and classes `.dex` into 1 cross-platform `.jar` file, compatible on both desktop and Android version *(refer to [**#Android**](#Android))*.

### \*nix
Same as above, but replace `gradlew` with `./gradlew`. <br>
If the program returns with `Permission Denied` or `Command Not Found`, execute `chmod +x ./gradlew` and the `./gradlew` should work fine. Do this only *once*.

### Android
#### Windows
1. Install the Android SDK [here](https://developer.android.com/studio). Make sure you're downloading the **"Command line tools only"**, as Android Studio is not required.
2. Create a folder with any name you want anywhere, then set `%ANDROID_SDK_ROOT%` environment variable to the created folder.
3. Unzip the downloaded Android SDK command line tools, then move the folder into `%ANDROID_SDK_ROOT%`.
    * Note that the downloaded command line tools folder is sometimes wrong; the correct path to `sdkmanager.bat` is `cmdline-tools/tools/bin/sdkmanager.bat`.
4. Open the command line, then `cd` to `%ANDROID_HOME%/cmdline-tools/tools/bin`.
5. Run `sdkmanager --install "build-tools;30.0.3"` to install the Android build tools, assuming you're using version `30.0.3`.
6. Add `%ANDROID_HOME%/build-tools/30.0.3` to your `PATH` environment variable.
7. Run `gradlew main:distDex`. This will create a `.jar` file in the build directory, playable in both desktop and Android.
