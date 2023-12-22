import { useState, useEffect } from 'react';
import axios from 'axios';
import styled from 'styled-components';
import Cookies from 'js-cookie';
import { useNavigate } from 'react-router-dom';
import Header1 from '../reuse/Header';
import Footer from '../reuse/Footer'

// Create styled components
const FormContainer = styled.form`
  display: flex;
  flex-direction: column;
  max-width: 300px;
  margin: auto;
  padding: 20px;
  border: 1px solid #ccc;
  border-radius: 5px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
`;

const CustomText = styled.div`
  font-family: 'Titan One', sans-serif;
`;

const Label = styled.label`
  margin-bottom: 10px;
`;

const Input = styled.input`
  width: 100%;
  padding: 8px;
  margin-bottom: 10px;
  border: 1px solid #ccc;
  border-radius: 4px;
`;

const Button = styled.button`
  background-color: #4caf50;
  color: white;
  padding: 10px;
  border: none;
  border-radius: 4px;
  cursor: pointer;

  &:hover {
    background-color: #45a049;
  }
`;

const ErrorMessage = styled.p`
  color: red;
  margin-top: 10px;
`;

const LoginForm = () => {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [errorMessage, setErrorMessage] = useState('');
  const navigate = useNavigate();

  useEffect(() => {
    const token = Cookies.get('token');
    if (token) {
      navigate('/campaigns');
    }
  }, []);

  const handleLogin = async (e) => {
    e.preventDefault();

    try {
      const response = await axios.post('http://localhost:8080/api/v1/auth/authenticate', {
        username,
        password,
      });

      const { token, refreshToken } = response.data;

      Cookies.set('token', token);
      Cookies.set('refreshToken', refreshToken);

      navigate("/campaigns");
    } catch (error) {
      console.error('Login failed', error);
      setErrorMessage('Invalid username or password');
    }
  };

  return (
    <FormContainer onSubmit={handleLogin}>
      <Header1 />
      <CustomText>Login</CustomText>
      <Label>
        Username:
        <Input type="text" value={username} onChange={(e) => setUsername(e.target.value)} />
      </Label>
      <Label>
        Password:
        <Input type="password" value={password} onChange={(e) => setPassword(e.target.value)} />
      </Label>
      <Button type="submit">Login</Button>
      {errorMessage && <ErrorMessage>{errorMessage}</ErrorMessage>}
      <Footer/>
    </FormContainer>
  );
};

export default LoginForm;
