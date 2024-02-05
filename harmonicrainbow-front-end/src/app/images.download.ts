async function downloadImages(URL: string, email: string, token: string | null): Promise<any> {
    return fetch(URL + email, {
        method: "GET",
        headers: {
            "Authorization": token ?? ''
        }
    })
}
async function downloadImage(URL: string, email: string, name: string, token: string): Promise<any> {
    const finalUrl = URL + `?email=${email}&name=${name}`
    const response = fetch(finalUrl, {
        method: "GET",
        headers: {
            "Authorization": token
        }
    })
    return response;
}

const _ = {
    downloadImage,
    downloadImages
};

export default _ ;