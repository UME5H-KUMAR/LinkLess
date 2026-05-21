 import React from "react";
import { FaFacebook, FaTwitter, FaInstagram, FaLinkedin } from "react-icons/fa";

const Footer = () => {
  return (
    <footer className="bg-custom-gradient text-white py-12 z-40 relative shadow-lg shadow-slate-700">
      <div className="container mx-auto px-6 lg:px-14 flex flex-col lg:flex-row lg:justify-between items-center gap-8">
        <div className="text-center lg:text-left">
          <h2 className="text-3xl font-bold mb-2 text-white">LinkLess</h2>
          <p className="text-gray-100">Simplifying URL shortening for efficient sharing</p>
        </div>

        <p className="mt-4 lg:mt-0 text-gray-100">
          &copy; 2024 LinkLess. All rights reserved.
        </p>

        <div className="flex space-x-6 mt-4 lg:mt-0">
          <a href="#" className="hover:text-yellow-200 transition-colors duration-150">
            <FaFacebook size={24} />
          </a>
          <a href="#" className="hover:text-yellow-200 transition-colors duration-150">
            <FaTwitter size={24} />
          </a>
          <a href="#" className="hover:text-yellow-200 transition-colors duration-150">
            <FaInstagram size={24} />
          </a>
          <a href="#" className="hover:text-yellow-200 transition-colors duration-150">
            <FaLinkedin size={24} />
          </a>
        </div>
      </div>
    </footer>
  );
};

export default Footer;