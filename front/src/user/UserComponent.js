// UserComponent.js
import React, { useState, useEffect } from 'react';
import axios from 'axios';
import Cookies from 'js-cookie';
import styled from 'styled-components';
import Header1 from '../reuse/Header';
import Footer from '../reuse/Footer';

const UsersContainer = styled.div`
  max-width: 600px;
  margin: 0 auto;
  padding: 20px;
  background-color: #f4f4f4;
  border-radius: 8px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
`;

const Title = styled.h1`
  color: #333;
  text-align: center;
`;

const UserList = styled.ul`
  list-style: none;
  padding: 0;
`;

const UserItem = styled.li`
  background-color: #fff;
  border: 1px solid #ddd;
  border-radius: 4px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  cursor: pointer;
  margin: 10px 0;
  padding: 10px;
  display: flex;
  justify-content: space-between;
  align-items: center;
`;

const ModalContainer = styled.div`
  background-color: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
  position: fixed;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  max-width: 400px;
  width: 100%;
`;

const FormContainer = styled.div`
  margin-top: 20px;
`;

const FormLabel = styled.label`
  margin-bottom: 10px;
  color: #555;
`;


const FormSelect = styled.select`
  padding: 8px;
  margin-bottom: 10px;
  border: 1px solid #ccc;
  border-radius: 4px;
`;

const FormButton = styled.button`
  background-color: #4caf50;
  color: white;
  border: none;
  padding: 10px;
  border-radius: 4px;
  cursor: pointer;

  &:hover {
    background-color: #45a049;
  }
`;

const DeleteButton = styled.button`
  background-color: #ff0000;
  color: white;
  border: none;
  padding: 10px;
  border-radius: 4px;
  cursor: pointer;

  &:hover {
    background-color: #cc0000;
  }
`;

const ErrorMessage = styled.div`
  color: #ff0000;
  margin-top: 10px;
`;

// Utility function for making authenticated requests
const makeAuthenticatedRequest = async (url, method, data = null) => {
  const token = Cookies.get('token');
  const refreshToken = Cookies.get('refreshToken');

  axios.defaults.headers.common['Authorization'] = `Bearer ${token}`;
  try {
    return await axios({
      method,
      url,
      data,
    });
  } catch (error) {
    if (error.response && error.response.status === 403) {
      // Token is invalid or expired, attempt refreshing
      try {
        const refreshResponse = await axios.get('http://localhost:8080/api/v1/auth/refresh', {
        headers: {
          Authorization: `Bearer ${refreshToken}`,
        }});

        const newAccessToken = refreshResponse.data.token;
        Cookies.set('token', newAccessToken);

        // Retry the original request with the new access token
        return await axios({
          method,
          url,
          data,
          headers: {
            Authorization: `Bearer ${newAccessToken}`,
          },
        });
      } catch (refreshError) {
        // Token refresh failed, redirect to login
        throw refreshError;
      }
    } else {
      // Other errors, rethrow the error
      throw error;
    }
  }
};

const UserComponent = () => {
  const [users, setUsers] = useState([]);
  const [userError, setError] = useState(null);
  const [selectedUser, setSelectedUser] = useState(null);
  const [showEditUserModal, setShowModal] = useState(false);
  const [role, setRole] = useState(null);

  useEffect( () => {
    const fetchData = async () => {
      try {
        const response = await makeAuthenticatedRequest(
          'http://localhost:8080/api/user/',
          'get'
        );
        setUsers(response.data);
      } catch (error) {
        console.error('Error fetching users:', error);
        setError('Error fetching users');
      }
    }
    fetchData();
  }, []); // Run this effect only once on component mount

  const handleDelete = async userId => {
    const token = Cookies.get('token');
    axios.defaults.headers.common['Authorization'] = `Bearer ${token}`;
    try {
      await makeAuthenticatedRequest(`http://localhost:8080/api/user/${userId}`, 'delete');
      setUsers(prevUsers =>
        prevUsers.filter(user => user.id !== userId));
        setError(null);
    } catch (error) {
      setError("Problem with deleting this user");
    }
  }

  const handlePatch = async(userId) =>
  {
    const token = Cookies.get('token');
    axios.defaults.headers.common['Authorization'] = `Bearer ${token}`;
    try {
      selectedUser.role = role;
      const response = await makeAuthenticatedRequest(`http://localhost:8080/api/user/${userId}`,'patch', selectedUser);
      console.log(response);
    } catch (error) {
      setError("Problem with changing user role");
    }
  }


  const showModal = async(user) => {
    setSelectedUser(user);
    setRole(user.role);
    setShowModal(true);
  }

  const hideModal = async() => {
    setShowModal(false);
  }

  return (
    <UsersContainer>
      <Header1 />
      <Title>User List</Title>
      <FormContainer>
      <UserList>
        {users.map(user => (
          <UserItem onClick={()=>showModal(user)} key={user.id}>
            {user.username} - {user.role}
            <DeleteButton onClick={() => handleDelete(user.id)}>Delete user</DeleteButton>
          </UserItem>
        ))}
      </UserList>
      {showEditUserModal && (
        <ModalContainer>
          <Title>Edit user</Title>
          <Title>{selectedUser.username}</Title>
          <FormLabel>Role:
          <FormSelect value={role}
          onChange={(e)=>setRole(e.target.value)}>
            <option value="PLAYER">Player</option>
            <option value="GAMEMASTER">Gamemaster</option>
            <option value="ADMIN">Admin</option>
          </FormSelect>
          {console.log(role)}
          </FormLabel>
          <FormButton onClick={()=>handlePatch(selectedUser.id)}>Save</FormButton>
          <FormButton onClick={()=>hideModal()}>Hide window</FormButton>
        </ModalContainer>
      )}
      {userError && <ErrorMessage>{userError}</ErrorMessage>}
      </FormContainer>
      <Footer />
    </UsersContainer>
  );
};

export default UserComponent;
