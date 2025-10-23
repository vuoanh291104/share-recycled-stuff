package services

type AuthService struct {
	springBootClient *SpringBootClient
}

func NewAuthService(client *SpringBootClient) *AuthService {
	return &AuthService{
		springBootClient: client,
	}
}

func (s *AuthService) ValidateToken(token string) (*TokenValidation, error) {
	return s.springBootClient.ValidateToken(token)
}
