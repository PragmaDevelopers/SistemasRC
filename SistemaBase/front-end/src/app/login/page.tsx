"use client"

export default function Page() {
    return (
        <main className="relative w-full h-full flex justify-center items-center">
            <h1 className="w-full absolute top-2 text-center">Login</h1>
            <form onSubmit={(e)=>{
                e.preventDefault();
                fetch("https://sistema-rc-e0ef46aabaec.herokuapp.com/api/public/login", {
                method: "POST",
                body: JSON.stringify({
                    email: "lucas@gmail.com",
                    password: "123"
                }),
                headers: {
                    "content-type": "application/json",
                },
                })
                .then(response => {
                    if (!response.ok) {
                    throw new Error(`HTTP error! Status: ${response.status}`);
                    }
                    return response.text(); // Alterado para response.json()
                })
                .then(data => {
                    console.log(data); // Aqui você terá o objeto JSON retornado pela API
                })
                .catch(error => {
                    console.error("Error fetching data:", error);
                });
            }} className="flex flex-col justify-center items-center">
                <div className="flex items-center">
                    <label className="mr-2" htmlFor="namelabel">Nome de usuário: </label>
                    <input id="namelabel" className="my-2 text-black" type="text" placeholder="Insira seu nome de usuário" />
                </div>
                <div className="flex items-center">
                    <label className="mr-2" htmlFor="namelabel">Senha do usuário: </label>
                    <input className="my-2 text-black" type="password" placeholder="Insira sua senha" />
                </div>
                <button className="p-2 border-2 rounded-md border-blue-400 bg-neutral-50 hover:bg-blue-400" type="submit">
                    Cadastrar
                </button>
            </form>
        </main>
    );
}
