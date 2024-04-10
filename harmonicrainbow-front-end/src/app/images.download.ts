async function downloadImages(URL: string, token: string | null): Promise<any> {
    return fetch(URL, {
        method: "GET",
        headers: {
            "Authorization": token ?? ''
        }
    })
}

async function downloadImage(URL: string, userId: string, name: string, token: string): Promise<any> {
    const finalUrl = URL + `?userId=${userId}&name=${name}`
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