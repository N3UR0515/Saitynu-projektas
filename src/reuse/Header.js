// Header.js

import React from 'react';
import styled from 'styled-components';
import Cookies from 'js-cookie';
import Menu from './Menu';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faHatCowboy } from '@fortawesome/free-solid-svg-icons';


const HeaderContainer = styled.div`
    background-color: #3498db;
    color: white;
    padding: 10px;
    display: flex;
    justify-content: space-between;
    align-items: center;
`;

const Logo = styled.h1`
  margin: 0;
  a {
    color: white;
    text-decoration: none;
  }
`;

const Navigation = styled.nav`
  ul {
    list-style: none;
    display: flex;
    gap: 20px;

    li {
      a {
        color: white;
        text-decoration: none;
        cursor: pointer; // Add cursor pointer to indicate it's clickable
      }
    }
  }
`;

const Title = styled.h1`
  color: #333;
  text-align: center;
`;


const Header1 = () => {
  const token = Cookies.get("token");

  return (
    <HeaderContainer>
      <Logo><a href="/login"><FontAwesomeIcon icon={faHatCowboy} /></a></Logo>
      <Navigation>
        <ul>
          {token ? (
            <>
            <Menu />
            </>
          ) : (
            <>
              <li><a href="/login">Login</a></li>
              <li><a href="/register">Register</a></li>
            </>
          )}
        </ul>
      </Navigation>
    </HeaderContainer>
  );
};

export default Header1;
