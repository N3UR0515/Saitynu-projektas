import { useState, useEffect } from 'react';
import styled from 'styled-components';
import axios from 'axios';
import Cookies from 'js-cookie';
import { useNavigate } from 'react-router-dom';
import Header1 from '../reuse/Header';
import Footer from '../reuse/Footer';

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

const RegistrationForm = () => {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [email, setEmail] = useState('');
  const [errorMessage, setErrorMessage] = useState('');
  const navigate = useNavigate();

  useEffect(() => {
    const token = Cookies.get('token');
    if (token) {
      navigate('/campaigns');
    }
  }, []);

  const handleRegister = async (e) => {
    e.preventDefault();

    try {
      const response = await axios.post('http://localhost:8080/api/v1/auth/register', {
        username,
        password,
        email
      });

      const { token, refreshToken } = response.data;

      Cookies.set('token', token);
      Cookies.set('refreshToken', refreshToken);

      console.log(token, refreshToken);

      navigate("/campaigns");
    } catch (error) {
      setErrorMessage('Username already taken');
      console.error('Registration failed', error);
    }
  };

  return (
    <FormContainer onSubmit={handleRegister}>
      <Header1 />
      <CustomText>Register</CustomText>
      <Label>
        Username:
        <Input type="text" value={username} onChange={(e) => setUsername(e.target.value)} />
      </Label>
      <Label>
        Email:
        <Input type="email" value={email} onChange={(e) => setEmail(e.target.value)} />
      </Label>
      <Label>
        Password:
        <Input type="password" value={password} onChange={(e) => setPassword(e.target.value)} />
      </Label>
      <Button type="submit">Register</Button>
      {errorMessage && <ErrorMessage>{errorMessage}</ErrorMessage>}
      <Footer/>
    </FormContainer>
  );
};

export default RegistrationForm;
