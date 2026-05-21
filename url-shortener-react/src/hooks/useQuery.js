import { useQuery } from "@tanstack/react-query"
import api from "../api/api"


export const useFetchMyShortUrls = (token, onError) => {
    return useQuery({
        queryKey: ["my-shortenurls"],
        queryFn: async () => {
            return await api.get(
                "/urls/myUrls",
                {
                    headers: {
                        "Content-Type": "application/json",
                        Accept: "application/json",
                        Authorization: "Bearer " + token,
                    },
                }
            );
        },
        select: (data) => {
            const sortedData = data.data.sort(
                (a, b) => new Date(b.createdDate) - new Date(a.createdDate)
            );
            return sortedData;
        },
        onError,
        staleTime: 5000
    });
};

export const useFetchTotalClicks = (token, onError) => {
    return useQuery({
        queryKey: ["url-totalclick"],
        queryFn: async () => {
            return await api.get(
                "/urls/totalClicks?startDate=2024-01-01T00:00:00&endDate=2025-12-31T23:59:59",
                {
                    headers: {
                        "Content-Type": "application/json",
                        Accept: "application/json",
                        Authorization: "Bearer " + token,
                    },
                }
            );
        },
        select: (data) => {
            const convertToArray = Object.keys(data.data).map((key) => ({
                clickDate: key,
                count: data.data[key],
            }));
            return convertToArray;
        },
        onError,
        staleTime: 5000
    });
};
