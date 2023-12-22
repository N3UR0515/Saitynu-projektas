import axios from 'axios';
import Cookies from 'js-cookie';
import { useNavigate } from 'react-router-dom';
import styled from 'styled-components';

const HypertextButton = styled.button`
  background: none;
  border: none;
  color: #ffff;
  text-decoration: none;
  cursor: pointer;
  font-size: inherit;
  padding: 0;
  margin: 0;
`;

const LogoutButton = () => {
  const navigate = useNavigate();
  const handleLogout = async () => {
    const refreshToken = Cookies.get('refreshToken');
    try {
      // Call your server-side logout endpoint
      axios.defaults.headers.common['Authorization'] = `Bearer ${refreshToken}`;
      await axios.get('http://localhost:8080/api/v1/auth/logout');

      Cookies.remove('token');
      Cookies.remove('refreshToken');

      navigate("/login");
      
    } catch (error) {
      console.error('Logout failed', error);
      // Handle logout failure
    }
  };

  return <HypertextButton onClick={handleLogout}>Logout</HypertextButton>;
};

export default LogoutButton;
