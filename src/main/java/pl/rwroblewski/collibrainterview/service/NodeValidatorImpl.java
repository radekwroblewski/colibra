package pl.rwroblewski.collibrainterview.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pl.rwroblewski.collibrainterview.exception.DuplicateException;
import pl.rwroblewski.collibrainterview.exception.NotFoundException;
import pl.rwroblewski.collibrainterview.exception.ValidationException;
import pl.rwroblewski.collibrainterview.repository.NodeRepository;

@Service
public class NodeValidatorImpl implements NodeValidator {

    @Autowired
    private NodeRepository nodeRepository;

    @Override
    public void validateNotExists(String nodeName) throws ValidationException {
        if (nodeRepository.containsNode(nodeName)) {
            throw new DuplicateException();
        }
    }

    @Override
    public void validateExists(String nodeName) throws ValidationException {
        if (!nodeRepository.containsNode(nodeName)) {
            throw new NotFoundException();
        }

    }
}
