package com.dataexchange.server.sshd;

import com.dataexchange.server.jpa.repository.AuthUserJpaRepository;
import com.dataexchange.server.jpa.repository.FileEventJpaRepository;
import com.dataexchange.server.jpa.model.FileEventEntity;
import org.apache.sshd.common.AttributeStore;
import org.apache.sshd.server.session.ServerSession;
import org.apache.sshd.server.subsystem.sftp.AbstractSftpEventListenerAdapter;
import org.apache.sshd.server.subsystem.sftp.FileHandle;
import org.apache.sshd.server.subsystem.sftp.Handle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Date;

@Component
public class TrackingSftpEventListener extends AbstractSftpEventListenerAdapter {

    private static final AttributeStore.AttributeKey<FileEventEntity> EVENT = new AttributeStore.AttributeKey<>();

    @Autowired
    private FileEventJpaRepository fileEventJpaRepository;

    @Autowired
    private AuthUserJpaRepository authUserJpaRepository;

    @Override
    public void open(ServerSession session, String remoteHandle, Handle localHandle) {
        Path path = localHandle.getFile();
        if (!Files.isDirectory(path)) {
            FileEventEntity event = new FileEventEntity();
            event.setAuthUser(authUserJpaRepository.findByUsername(session.getUsername()));
            event.setDateStarted(new Date());
            event.setFilename(path.toString());
            event = fileEventJpaRepository.save(event);

            session.setAttribute(EVENT, event);
        }

        super.open(session, remoteHandle, localHandle);
    }

    @Override
    public void writing(ServerSession session, String remoteHandle, FileHandle localHandle, long offset, byte[] data, int dataOffset, int dataLen) throws IOException {
        FileEventEntity event = session.getAttribute(EVENT);
        if (event.getAction() == null) {
            event.setAction("WRITE");
            fileEventJpaRepository.save(event);
        }

        super.writing(session, remoteHandle, localHandle, offset, data, dataOffset, dataLen);
    }

    @Override
    public void reading(ServerSession session, String remoteHandle, FileHandle localHandle, long offset, byte[] data, int dataOffset, int dataLen) throws IOException {
        FileEventEntity event = session.getAttribute(EVENT);
        if (event.getAction() == null) {
            event.setAction("READ");
            fileEventJpaRepository.save(event);
        }
        if (offset >= Files.size(localHandle.getFile())) {
            event.setDateFinished(new Date());
            fileEventJpaRepository.save(event);
        }

        super.reading(session, remoteHandle, localHandle, offset, data, dataOffset, dataLen);
    }

    @Override
    public void close(ServerSession session, String remoteHandle, Handle localHandle) {
        FileEventEntity event = session.removeAttribute(EVENT);
        if (event != null && "WRITE".equals(event.getAction())) {
            event.setDateFinished(new Date());
            fileEventJpaRepository.save(event);
        }

        super.close(session, remoteHandle, localHandle);
    }

    @Override
    public void moved(ServerSession session, Path srcPath, Path dstPath, Collection<CopyOption> opts, Throwable thrown) throws IOException {
        FileEventEntity event = new FileEventEntity();
        event.setFilename(srcPath.toString());
        event.setDateStarted(new Date());
        event.setDateFinished(new Date());
        event.setAuthUser(authUserJpaRepository.findByUsername(session.getUsername()));
        event.setAction("MOVED_DELETED");
        fileEventJpaRepository.save(event);

        FileEventEntity event2 = new FileEventEntity();
        event2.setFilename(dstPath.toString());
        event2.setDateStarted(new Date());
        event2.setDateFinished(new Date());
        event2.setAuthUser(authUserJpaRepository.findByUsername(session.getUsername()));
        event2.setAction("MOVED_NEW");
        fileEventJpaRepository.save(event2);

        super.moved(session, srcPath, dstPath, opts, thrown);
    }

    @Override
    public void removed(ServerSession session, Path path, Throwable thrown) throws IOException {
        FileEventEntity event = new FileEventEntity();
        event.setFilename(path.toString());
        event.setDateStarted(new Date());
        event.setDateFinished(new Date());
        event.setAuthUser(authUserJpaRepository.findByUsername(session.getUsername()));
        event.setAction("DELETED");
        fileEventJpaRepository.save(event);

        super.removed(session, path, thrown);
    }
}
