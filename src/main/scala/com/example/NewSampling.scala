package com.example

import breeze.linalg._
import scalismo.common.{DiscreteField, UnstructuredPointsDomain}
import scalismo.geometry.{Landmark, Point3D, Vector, _3D}
import scalismo.statisticalmodel.{DiscreteGaussianProcess, DiscreteLowRankGaussianProcess}
import breeze.stats.distributions.{Gaussian, Uniform}
import scalismo.utils.Random

import scala.math.{pow, sqrt}

class NewSampling(gaussProc : DiscreteLowRankGaussianProcess[_3D, UnstructuredPointsDomain[_3D], Vector[_3D]]) {

  //In this method we sample uniformly each coefficient of the PCA between -limits and limit
  def sampleUniform(limits: Double)(implicit random: Random): DiscreteField[_3D, UnstructuredPointsDomain[_3D], Vector[_3D]] = {
    val uniformL = Uniform(-limits, limits)(random.breezeRandBasis)
    val coeffs = uniformL.sample(gaussProc.rank)
    gaussProc.instance(DenseVector(coeffs.toArray))
  }

  //In this method we sample randomly following a normal distribution of mean 0 and std 1,2,3 etc
  def sampleGaussian(std : Int)(implicit random: Random): DiscreteField[_3D, UnstructuredPointsDomain[_3D], Vector[_3D]] = {
    val standardNormal = Gaussian(0, std)(random.breezeRandBasis)
    val coeffs = standardNormal.sample(gaussProc.rank)
    gaussProc.instance(DenseVector(coeffs.toArray))
  }

  //In this method we sample randomly following a particular distribution
}