import React, { useEffect, useState } from "react";
import { getAllActiveJobs, updateJobStatus } from "../../Services/jobService";
import { Loader } from "../../components/ui/Loader";
import { handleGlobalError } from "../../Services/GlobalExceptionService";
import { useAuthUserContext } from "../../Contexts/AuthUserContext";
import { useNavigate } from "react-router-dom";
import { toast } from "react-toastify";
import { useQuery, useQueryClient } from "@tanstack/react-query";
import {
  Dialog,
  DialogClose,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog";
import { Button } from "@/components/ui/button";
import ShareJob from "./ShareJob";
import ReferrJob from "./ReferraJob";

function Jobs() {
  const [jobs, setJobs] = useState([]);
  const [filteredJobs, setFilteredJobs] = useState([]);
  const [loading, setLoading] = useState(false);
  const { authUser, setAuthUser } = useAuthUserContext();
  const [statusFilter, setStatusFilter] = useState();
  const navigate = useNavigate();
  const queryClient = useQueryClient();
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [isLastPage, setIsLastPage] = useState(false);

  const { data, error, isPending, isError, isSuccess } = useQuery({
    queryKey: ["job-Openings", currentPage],
    queryFn: () => getAllActiveJobs(currentPage),
    staleTime: 5 * 20 * 600,
  });

  useEffect(() => {
    setJobs(data?.content);
    setFilteredJobs(data?.content);
    setTotalPages(data?.totalPages);
    setIsLastPage(data?.last);
  }, [data]);

  const closeJobOpening = async (jobId) => {
    try {
      await updateJobStatus(jobId, "CLOSED");
      toast.success("Job Closed SuccessFully!!");
      queryClient.invalidateQueries({ queryKey: ["job-Openings"] });
    } catch (e) {
      handleGlobalError(e);
    }
  };
  const reOpenJobOpening = async (jobId) => {
    try {
      await updateJobStatus(jobId, "ACTIVE");
      toast.success("Job Re-Opened SuccessFully!!");
      queryClient.invalidateQueries({ queryKey: ["job-Openings"] });
    } catch (e) {
      handleGlobalError(e);
    }
  };
  const handleFilterChange = (status) => {
    if (status == "") {
      setFilteredJobs(jobs);
      return;
    }
    setFilteredJobs((prev) => {
      return jobs.filter((job) => {
        return job.status == status;
      });
    });
  };
  if (isError && error) {
    console.log(error);
    handleGlobalError(error);
  }
  if (isPending)
    return <div className="text-center mt-20">Loading jobOpenings...</div>;
  if (!isPending && jobs?.length == 0) {
    return <p>Job Openings not found !!</p>;
  }
  const handlePreviousPage = () => {
    if (currentPage > 0) {
      setCurrentPage((prev) => prev - 1);
    }
  };

  const handleNextPage = () => {
    if (!isLastPage) {
      setCurrentPage((prev) => prev + 1);
    }
  };

  return (
    <>
      {loading ? (
        <Loader size={32} />
      ) : (
        <>
          <div className="max-w-4xl mx-auto space-y-6 w-full bg-gray-100 p-6">
            <div className="bg-white rounded-2xl shadow p-6">
              <div className="grid grid-cols-1">
                <h2 className="text-2xl text-center font-semibold mb-4 flex justify-around">
                  Job-Openings
                </h2>

                <div className="flex justify-between mb-3">
                  <div className="grid grid-cols-1 md:grid-cols-5 gap-4">
                    <select
                      name="status"
                      value={statusFilter}
                      onChange={(e) => handleFilterChange(e.target.value)}
                      className="border rounded px-3 py-2 text-sm bg-white"
                    >
                      <option value="">All</option>
                      <option value="ACTIVE">Active</option>
                      <option value="CLOSED">Closed</option>
                    </select>
                  </div>
                  {authUser.role == "HR" && (
                    <button
                      onClick={() => {
                        navigate(`Create`);
                      }}
                      className="w-auto bg-black text-white font-medium py-2  px-3 rounded-2xl "
                    >
                      Create Job-Opening
                    </button>
                  )}
                </div>
                <div className="space-y-4">
                  {filteredJobs?.length === 0 ? (
                    <div className="text-center py-10 text-gray-500">
                      {" "}
                      "No active jobs yet!!!"{" "}
                    </div>
                  ) : (
                    filteredJobs?.map((job) => (
                      <div
                        key={job.id}
                        className="bg-white shadow rounded-lg p-4 border border-gray-200 flex items-start gap-4"
                      >
                        <div className="flex-1">
                          <p className="text-sm text-gray-500">Title</p>
                          <p className="font-medium text-gray-800">
                            {job.title}
                          </p>
                          <p className="text-sm text-gray-500">Hr-Name</p>
                          <p className="font-medium text-gray-800">
                            {job.hrOwnerName}
                          </p>
                          <p className="text-sm text-gray-500">Status</p>
                          <p className="font-medium text-gray-800">
                            {job.status}
                          </p>
                          <p className="text-sm text-gray-500">Summary</p>
                          <p className="font-medium text-gray-800">
                            {job.summary}
                          </p>
                          <p className="text-sm text-gray-500">Description</p>
                          <p className="font-medium text-gray-800">
                            {job.description}
                          </p>
                          <div className="flex flex-row gap-2.5">
                            <a
                              href={job.jdFileUrl}
                              target="_blank"
                              className="w-auto px-3 mt-4 py-1 bg-black text-white text-sm rounded hover:bg-gray-700"
                            >
                              View Job-Description
                            </a>
                            {job.status == "ACTIVE" ? (
                              <>
                                <Dialog>
                                  <DialogTrigger>
                                    <button
                                  className="w-auto bg-black text-white text-sm py-1 px-3 mt-4 rounded  hover:bg-gray-700 "
                                >
                                  Share
                                </button>
                                  </DialogTrigger>
                                  <DialogContent>
                                    <ShareJob jobId = {job.id}  />
                                    <DialogFooter className="sm:justify-start">
                                      <DialogClose asChild>
                                        <Button
                                          type="button"
                                          className="w-full"
                                        >
                                          Close
                                        </Button>
                                      </DialogClose>
                                    </DialogFooter>
                                  </DialogContent>
                                </Dialog>
                                 <Dialog>
                                  <DialogTrigger>
                                    <button
                                  className="w-auto bg-black text-white text-sm py-1 px-3 mt-4 rounded  hover:bg-gray-700 "
                                >
                                  Reffer
                                </button>
                                  </DialogTrigger>
                                  <DialogContent>
                                    <ReferrJob jobId = {job.id}  />
                                    <DialogFooter className="sm:justify-start">
                                      <DialogClose asChild>
                                        <Button
                                          type="button"
                                          className="w-full"
                                        >
                                          Close
                                        </Button>
                                      </DialogClose>
                                    </DialogFooter>
                                  </DialogContent>
                                </Dialog>
                                
                                {authUser.role == "HR" && (
                                  <>
                                    <button
                                      onClick={() => closeJobOpening(job.id)}
                                      className="w-auto bg-black text-white text-sm py-1 px-3 mt-4 rounded  hover:bg-gray-700 "
                                    >
                                      Close Job-Opening
                                    </button>
                                  </>
                                )}
                                <button
                                  onClick={() =>
                                    navigate(`/jobOpening/referrals/${job.id}`)
                                  }
                                  className="w-auto bg-black text-white text-sm py-1 px-3 mt-4 rounded  hover:bg-gray-700 "
                                >
                                  referrals
                                </button>
                              </>
                            ) : (
                              <button
                                onClick={() => reOpenJobOpening(job.id)}
                                className="w-auto bg-black text-white text-sm py-1 px-3 mt-4 rounded  hover:bg-gray-700 "
                              >
                                Re-Open Job-Opening
                              </button>
                            )}
                          </div>
                        </div>
                      </div>
                    ))
                  )}
                </div>
                {!isPending && jobs?.length > 0 && (
                  <div className="flex justify-between items-center mt-8 pt-4 border-t border-gray-200">
                    <button
                      onClick={handlePreviousPage}
                      disabled={currentPage === 0}
                      className={`px-4 py-2 rounded font-medium ${
                        currentPage === 0
                          ? "bg-gray-200 text-gray-400 cursor-not-allowed"
                          : "bg-black text-white hover:bg-gray-800"
                      }`}
                    >
                      Previous
                    </button>

                    <span className="text-sm text-gray-600 font-medium">
                      Page {currentPage + 1} of{" "}
                      {totalPages === 0 ? 1 : totalPages}
                    </span>

                    <button
                      onClick={handleNextPage}
                      disabled={isLastPage}
                      className={`px-4 py-2 rounded font-medium ${
                        isLastPage
                          ? "bg-gray-200 text-gray-400 cursor-not-allowed"
                          : "bg-black text-white hover:bg-gray-800"
                      }`}
                    >
                      Next
                    </button>
                  </div>
                )}
              </div>
            </div>
          </div>
        </>
      )}
    </>
  );
}

export default Jobs;
