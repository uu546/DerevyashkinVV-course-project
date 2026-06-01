package project.warehouse.mediator.interfaces;

import project.warehouse.control.dto.AuthRequest;
import project.warehouse.control.dto.AuthResponse;
import project.warehouse.control.dto.RegisterRequest;

public interface IAuthService {

    AuthResponse login(AuthRequest request);

    AuthResponse register(RegisterRequest request);

}
