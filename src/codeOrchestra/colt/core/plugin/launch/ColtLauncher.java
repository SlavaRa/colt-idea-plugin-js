package codeOrchestra.colt.core.plugin.launch;

import codeOrchestra.colt.core.plugin.ColtSettings;
import com.intellij.execution.ExecutionException;
import com.intellij.openapi.util.SystemInfo;

import java.io.File;
import java.io.IOException;

/**
 * @author Alexander Eliseyev
 */
public final class ColtLauncher {

    public static Process launch(String projectPath) throws ColtPathNotConfiguredException, ExecutionException, IOException {
        if (!ColtSettings.getInstance().isColtPathValid()) {
            throw new ColtPathNotConfiguredException();
        }

        File coltBaseDir = new File(ColtSettings.getInstance().getColtPath());

        if (SystemInfo.isMac && coltBaseDir.getPath().endsWith(".app")) {
            return new ProcessBuilder("open", "-n", "-a", coltBaseDir.getPath(), "--args",  projectPath, "-plugin:WS").start();
        } else if (SystemInfo.isWindows || SystemInfo.isLinux) {
            File executable = getApplicationExecutable(coltBaseDir);
            if (executable != null && executable.exists()) {
                return new ProcessBuilder(executable.getPath(), projectPath, "-plugin:WS").start();
            }

            throw new IllegalStateException("Can't locate the COLT executable");
        } else {
            throw new IllegalStateException("Unsupported OS: " + System.getProperty("os.name"));
        }
    }

    public static File getApplicationExecutable(File coltBaseDir) {
        if (SystemInfo.isMac) {
            File executable = coltBaseDir;
            return executable.exists() ? executable : null;
        } else if (SystemInfo.isWindows) {
            if(coltBaseDir.isFile()) {
                return coltBaseDir.exists() ? coltBaseDir : null;
            } else {
                File executable = new File(coltBaseDir, "colt.exe");
                return executable.exists() ? executable : null;
            }
        } else if (SystemInfo.isLinux) {
            if(coltBaseDir.isFile()) {
                return coltBaseDir.exists() ? coltBaseDir : null;
            } else {
                File executable = new File(coltBaseDir, "colt");
                return executable.exists() ? executable : null;
            }
        }

        throw new IllegalStateException("Unsupported OS: " + System.getProperty("os.name"));
    }

}
