package pl.rwroblewski.collibrainterview.controller;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.socket.WebSocketSession;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import pl.rwroblewski.collibrainterview.exception.ValidationException;
import pl.rwroblewski.collibrainterview.repository.SessionRepository;
import pl.rwroblewski.collibrainterview.service.NodeService;

public class MessageHandlerTest {

    @InjectMocks
    private MessageHandler handler = new MessageHandlerImpl();

    @Mock
    private SessionRepository sessionRepository;

    @Mock
    private NodeService nodeService;

    private WebSocketSession session;

    @BeforeMethod
    private void init() {
        MockitoAnnotations.initMocks(this);
        session = mock(WebSocketSession.class);
    }

    @Test
    public void testSignin() throws ValidationException {
        String response = handler.handleMessage(session, "HI, I'M Radek\n");
        assertEquals(response, "HI Radek");
        verify(sessionRepository).setSessionName(session, "Radek");
    }

    @Test
    public void testSignout() throws ValidationException {
        String response = handler.handleMessage(session, "BYE MATE!\n");
        assertNull(response);
        verify(sessionRepository).terminateSession(session);
    }

    @Test
    public void testAddNode() throws ValidationException {
        String response = handler.handleMessage(session, "ADD NODE TestowyWezel\n");
        assertNull(response);
        verify(nodeService).addNode("TestowyWezel");
    }

    @Test
    public void testAddEdge() throws ValidationException {
        String response = handler.handleMessage(session, "ADD EDGE TestowyWezel1 TestowyWezel2 7213\n");
        assertNull(response);
        verify(nodeService).addEdge("TestowyWezel1", "TestowyWezel2", 7213);
    }

    @Test
    public void testRemoveNode() throws ValidationException {
        String response = handler.handleMessage(session, "REMOVE NODE TestowyWezel\n");
        assertNull(response);
        verify(nodeService).removeNode("TestowyWezel");
    }

    @Test
    public void testRemoveEdge() throws ValidationException {
        String response = handler.handleMessage(session, "REMOVE EDGE TestowyWezel1 TestowyWezel2\n");
        assertNull(response);
        verify(nodeService).removeEdge("TestowyWezel1", "TestowyWezel2");
    }

}
