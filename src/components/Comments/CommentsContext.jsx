import {createContext, useState,useEffect } from 'react';


export const CommentsContext = createContext();

export const SelectedCommentProvider = ({ children }) => {


    const [postId, setPostId] = useState();
    const [currentCommentText, setcurrentCommentText] = useState("");
    const [backendComments, setBackendComments] = useState([]);
    const [currentPost, setCurrentPost] = useState({});

    useEffect(() => {
        console.log(currentPost);
    
      }, [currentPost]);


  useEffect(() => {
    setBackendComments([]);

  }, []);
    


    return (
        <CommentsContext.Provider value={{ postId, setPostId, currentCommentText, setcurrentCommentText }}>
            {children}
        </CommentsContext.Provider>
    );
};
