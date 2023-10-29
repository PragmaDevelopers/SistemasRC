import { useState,useEffect } from 'react';

export default function AutoWidthInput({text,className}:{text:string,className:string}) {
  const [inputValue, setInputValue] = useState('');
  const [inputMinWidth, setInputMinWidth] = useState(100);
  useEffect(()=>{
    setInputValue(text);
    setInputMinWidth(text.length * 9)
  },[])

  const handleChange = (value:string) => {
    setInputValue(value);
    setInputMinWidth(value.length * 9)
  };

  const minWidth = inputMinWidth < 15 ? 20 : inputMinWidth; // Largura mínima desejada
  const maxWidth = 500; // Largura máxima desejada

  const inputStyle = {
    width: `${Math.min(Math.max(inputValue.length * 9, minWidth), maxWidth)}px`
  };

  return (
    <input
      type="text"
      value={inputValue}
      onChange={(e)=>handleChange(e.target.value)}
      style={inputStyle}
      className={className}
    />
  );
}


