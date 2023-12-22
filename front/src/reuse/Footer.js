// Footer.js

import React from 'react';
import styled from 'styled-components';

const FooterContainer = styled.footer`
  background-color: #333;
  color: white;
  padding: 20px;
  text-align: center;
  margin: 10px 0 0 0;
`;

const Footer = () => {
  return (
    <FooterContainer>
      <div>
        <p>Do not contact us</p>
      </div>
      <div>
        <p>&copy; 2023 My Company. All rights reserved.</p>
      </div>
    </FooterContainer>
  );
};

export default Footer;
