package com.dataexchange.server.sshd.file;

import org.apache.sshd.common.file.root.RootedFileSystemProvider;
import org.apache.sshd.common.file.virtualfs.VirtualFileSystemFactory;
import org.apache.sshd.common.session.Session;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.Map;

public class UserRootedFileSystemFactory extends VirtualFileSystemFactory {

    public UserRootedFileSystemFactory(Path homeDir) {
        super(homeDir);
    }

    @Override
    public FileSystem createFileSystem(Session session) throws IOException {
        final Map<String, ?> env = CustomFileSystemProvider.ENV;
        final String username = session.getUsername();
        final Path dir = computeRootDir(session);

        if (dir == null) {
            throw new InvalidPathException(username, "Cannot resolve home directory");
        }

        return new RootedFileSystemProvider().newFileSystem(dir, env);
    }

    @Override
    public Path getUserHomeDir(String username) {
        Path userHomeDir = super.getUserHomeDir(username);
        if (userHomeDir == null) {
            Path userDir = getDefaultHomeDir().resolve(username);
        
            if (Files.notExists(userDir)) {
                try {
                    Files.createDirectory(userDir);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            setUserHomeDir(username, userDir);

            userHomeDir = userDir;
        }

        return userHomeDir;
    }
}
