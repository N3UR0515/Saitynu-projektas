import React, { useState, useEffect } from 'react';
import styled from 'styled-components';
import LogoutButton from '../auth/LogoutButton';

const Navbar = styled.nav`
  background-color: #3498db;
  padding: 15px; /* Adjust the padding to make the navbar taller */
  display: flex;
  justify-content: space-between;
  align-items: center;
  position: sticky;
  top: 60px; /* Adjust this value based on your header's height */
  z-index: 100; /* Add a z-index to make sure it's above other content */
`;


const NavList = styled.ul`
  list-style: none;
  display: flex;
  gap: 20px;

  @media (max-width: 768px) {
    display: ${(props) => (props.showMenu ? 'flex' : 'none')};
    flex-direction: column;
    position: absolute;
    top: 60px;
    left: 50%;
    transform: translateX(-50%);
    width: 100%;
    max-width: 200px; /* Set a max-width or adjust as needed */
    background-color: #3498db;
    padding: 10px;
  }
`;

const NavItem = styled.li`
text-align: left; /* Align text to the left */
  a {
    color: white;
    text-decoration: none;
    cursor: pointer;
  }
`;



const HamburgerIcon = styled.div`
  cursor: pointer;
  font-size: 24px;
  color: white;
  padding: 10px;

  @media (min-width: 769px) {
    display: none;
  }
`;



const Menu = () => {
  const [showMenu, setShowMenu] = useState(true);

  const toggleMenu = () => {
    setShowMenu(!showMenu);
  };

  useEffect(() => {
    const handleResize = () => {
      if(window.innerWidth > 768)
        setShowMenu(true);
    };
  
    handleResize(); // Call it once to set initial state
  
    window.addEventListener('resize', handleResize);
  
    return () => {
      window.removeEventListener('resize', handleResize);
    };
  }, []);

  return (
    <Navbar>
      <NavList style={{ display: showMenu ? 'flex' : 'none' }}>
        <NavItem><a href="/campaigns">Campaigns</a></NavItem>
        <NavItem><LogoutButton /></NavItem>
      </NavList>
      <HamburgerIcon onClick={toggleMenu}>☰</HamburgerIcon>
    </Navbar>
  );
};

export default Menu;
